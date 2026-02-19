pipeline { 
    agent any

    environment {
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-17'
        MVN_HOME = 'C:\\Program Files\\apache-maven-3.9.12'
        TOMCAT_PATH = 'C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1'
        APP_NAME = 'HOOT'
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

                echo "Starting Tomcat in background..."
                // Start Tomcat in background, non-blocking
                bat "start \"Tomcat\" \"${TOMCAT_PATH}\\bin\\startup.bat\""

                echo "Deploy stage completed. Tomcat is running in background. ✅"
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
