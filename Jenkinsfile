pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "hoot-app:latest"
        CONTAINER_NAME = "hoot-container"
        HOST_PORT = 8082          // host वर कुठे run करायचं
        CONTAINER_PORT = 8080     // container मध्ये app port
        APP_URL = "http://localhost:8082/HOOT/" // host वर access URL
        MAX_RETRIES = 30
        WAIT_TIME = 5             // seconds
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out code..."
                git branch: 'master', url: 'https://github.com/mysoftware201/cicid-project.git'
            }
        }

        stage('Docker Build & Deploy') {
            steps {
                echo "Building Docker image..."
                bat "docker build -t ${DOCKER_IMAGE} ."

                echo "Stopping existing container (if any)..."
                bat "docker rm -f ${CONTAINER_NAME} || echo 'No existing container to remove'"

                echo "Running new container on host port ${HOST_PORT}..."
                bat "docker run -d --name ${CONTAINER_NAME} -p ${HOST_PORT}:${CONTAINER_PORT} ${DOCKER_IMAGE}"

                echo "Waiting for application to be ready..."
                script {
                    def appUp = false
                    for (int i = 1; i <= MAX_RETRIES; i++) {
                        try {
                            def response = powershell(
                                returnStdout: true, 
                                script: "try { Invoke-WebRequest -Uri '${APP_URL}' -UseBasicParsing -TimeoutSec 5; 'OK' } catch { 'FAIL' }"
                            ).trim()

                            if (response == 'OK') {
                                appUp = true
                                echo "Application is up and running! ✅"
                                break
                            }
                        } catch (err) {
                            // ignore exceptions
                        }

                        echo "Waiting for app to start... (${i}/${MAX_RETRIES})"
                        sleep WAIT_TIME
                    }

                    if (!appUp) {
                        error "Application did not start in expected time! ❌"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully! 🎉"
        }
        failure {
            echo "Pipeline failed! ❌"
        }
    }
}
