pipeline { 
    agent any

    environment {
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-17'
        MVN_HOME = 'C:\\Program Files\\apache-maven-3.9.12'
        TOMCAT_PATH = 'C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1'
        APP_NAME = 'HOOT'
        TOMCAT_PORT = 8080  // check this port for readiness
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/mysoftware201/cicid-project.git'
            }
        }

        stage('Build') {
            steps {
                echo "Building WAR..."
                bat "\"${MVN_HOME}\\bin\\mvn\" clean package -DskipTests"
            }
        }

        stage('Test') {
            steps {
                echo "Running Tests..."
                bat "\"${MVN_HOME}\\bin\\mvn\" test"
            }
            post {
                always {
                    junit '**\\target\\surefire-reports\\*.xml'
                }
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo "Stopping Tomcat..."
                bat "\"${TOMCAT_PATH}\\bin\\shutdown.bat\""
                sleep 10  // wait to fully stop Tomcat

                echo "Backing up old WAR..."
                script {
                    def timestamp = new Date().format("yyyyMMdd_HHmmss")
                    bat "if exist \"${TOMCAT_PATH}\\webapps\\${APP_NAME}.war\" copy \"${TOMCAT_PATH}\\webapps\\${APP_NAME}.war\" \"${TOMCAT_PATH}\\webapps\\${APP_NAME}_backup_${timestamp}.war\""
                }

                echo "Deleting old app..."
                bat "if exist \"${TOMCAT_PATH}\\webapps\\${APP_NAME}\" rmdir /s /q \"${TOMCAT_PATH}\\webapps\\${APP_NAME}\""
                bat "if exist \"${TOMCAT_PATH}\\webapps\\${APP_NAME}.war\" del /q \"${TOMCAT_PATH}\\webapps\\${APP_NAME}.war\""

                echo "Deploying new WAR..."
                bat "copy \"target\\${APP_NAME}.war\" \"${TOMCAT_PATH}\\webapps\\${APP_NAME}.war\""

                echo "Starting Tomcat (non-blocking)..."
                bat "start \"Tomcat\" \"${TOMCAT_PATH}\\bin\\startup.bat\""

                echo "Waiting for Tomcat port ${TOMCAT_PORT} to be open..."
                script {
                    def maxRetries = 30
                    def waitTime = 5
                    def portOpen = false

                    for (int i = 0; i < maxRetries; i++) {
                        try {
                            def socket = new Socket("localhost", TOMCAT_PORT)
                            socket.close()
                            portOpen = true
                            break
                        } catch (err) {
                            echo "Waiting for Tomcat port ${TOMCAT_PORT} to open... (${i+1}/${maxRetries})"
                        }
                        sleep waitTime
                    }

                    if (!portOpen) {
                        error "Tomcat did not start in expected time!"
                    } else {
                        echo "Tomcat is up and running! ✅"
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
