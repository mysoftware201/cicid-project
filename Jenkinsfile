pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "hoot-app:latest"
        APP_NAME = 'HOOT'
        APP_PORT = 9090                    // Host port for Docker
        CONTAINER_PORT = 8080              // Container's internal port
        APP_URL = "http://localhost:9090/HOOT/"
        MAX_RETRIES = 30                   // Integer, no quotes
        WAIT_TIME = 5                      // Integer, no quotes
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
                bat "docker build -t ${DOCKER_IMAGE} ."

                echo "Stopping existing container (if any)..."
                bat "docker rm -f hoot-container || echo 'No existing container to remove'"

                echo "Running new container on port ${APP_PORT}..."
                bat "docker run -d --name hoot-container -p ${APP_PORT}:${CONTAINER_PORT} ${DOCKER_IMAGE}"

                echo "Waiting for application to be ready..."
                script {
                    def maxRetries = env.MAX_RETRIES.toInteger()
                    def waitTime = env.WAIT_TIME.toInteger()
                    def appUp = false

                    for (int i = 0; i < maxRetries; i++) {
                        try {
                            def response = powershell(returnStdout: true, script: """
                                try { 
                                    Invoke-WebRequest -Uri '${APP_URL}' -UseBasicParsing -TimeoutSec 5
                                    'OK'
                                } catch { 'FAIL' }
                            """).trim()

                            if (response == 'OK') {
                                appUp = true
                                break
                            }
                        } catch (err) {
                            // ignore errors during retries
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
