pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "hoot-app:latest"
        APP_NAME = 'HOOT'
        APP_URL = 'http://localhost:9090/HOOT/' // URL to check if app is up
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/mysoftware201/cicid-project.git'
            }
        }

        stage('Docker Build & Deploy') {
            steps {
                echo "Building Docker image with WAR..."
                
                // Build Docker image using Dockerfile in project root
                bat "docker build -t ${DOCKER_IMAGE} ."

                echo "Stopping existing container (if any)..."
                bat "docker rm -f hoot-container || echo 'No existing container to remove'"

                echo "Running new container on 9090..."
                bat "docker run -d --name hoot-container -p 9090:8080 ${DOCKER_IMAGE}"

                echo "Waiting for application to be ready..."
                script {
                    def maxRetries = 30
                    def waitTime = 5
                    def appUp = false
                    for (int i = 0; i < maxRetries; i++) {
                        try {
                            def response = powershell(returnStdout: true, script: "try { Invoke-WebRequest -Uri '${APP_URL}' -UseBasicParsing -TimeoutSec 5; 'OK' } catch { 'FAIL' }").trim()
                            if (response == 'OK') {
                                appUp = true
                                break
                            }
                        } catch (err) {
                            // ignore
                        }
                        echo "Waiting for app to start... (${i+1}/${maxRetries})"
                        sleep waitTime
                    }
                    if (!appUp) {
                        error "Application did not start in expected time!"
                    } else {
                        echo "Application is up and running! ✅"
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully! 🎉'
        }
        failure {
            echo 'Pipeline failed! ❌'
        }
    }
}
