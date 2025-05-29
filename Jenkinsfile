pipeline {
    agent any

    environment {
        DOCKERHUB_USERNAME = 'juansmc'
        DOCKER_CREDENTIALS_ID = 'dockerhub-creds'
        KUBE_NAMESPACE = 'development'
        KUBE_MANIFESTS_DIR = 'k8s'
        SERVICES = ['api-gateway', 'user-service', 'cloud-config', 'order-service', 'payment-service', 'shipping-service', 'product-service', 'service-discovery', 'favourite-service']
    }

    stages {
        stage('1. Checkout') {
            steps {
                echo "Clonando el repositorio..."
                checkout scm
                echo "Checkout completado para la rama: ${env.BRANCH_NAME}"
            }
        }

        stage('2. Build, Test, Docker & Push') {
            steps {
                script {
                    SERVICES.each { service ->
                        def buildPath = "${service}/pom.xml"
                        if (fileExists(buildPath)) {
                            def imageName = "${DOCKERHUB_USERNAME}/${service}-ecommerce-boot"
                            def imageTag = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"

                            echo "=== Procesando ${service} ==="

                            dir(service) {
                                if (env.BRANCH_NAME == 'stage') {
                                    sh 'mvn clean verify'
                                } else {
                                    sh 'mvn clean package -DskipTests'
                                }

                                docker.withRegistry('https://registry.hub.docker.com', DOCKER_CREDENTIALS_ID) {
                                    def customImage = docker.build("${imageName}:${imageTag}", ".")
                                    customImage.push()
                                }

                                // Actualizar manifiestos
                                def deploymentFile = "${env.WORKSPACE}/${KUBE_MANIFESTS_DIR}/${service}-container-deployment.yaml"
                                if (fileExists(deploymentFile)) {
                                    sh "sed -i 's|image: .*|image: ${imageName}:${imageTag}|g' ${deploymentFile}"
                                }
                            }
                        } else {
                            echo "${service} no tiene pom.xml, omitiendo..."
                        }
                    }
                }
            }
        }

        stage('3. Deploy to K8s') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    SERVICES.each { service ->
                        def svcFile = "${KUBE_MANIFESTS_DIR}/${service}-service.yaml"
                        def depFile = "${KUBE_MANIFESTS_DIR}/${service}-container-deployment.yaml"

                        if (fileExists(svcFile) && fileExists(depFile)) {
                            echo "Desplegando ${service}..."
                            sh "kubectl apply -f ${svcFile} -n ${KUBE_NAMESPACE}"
                            sh "kubectl apply -f ${depFile} -n ${KUBE_NAMESPACE}"
                            sh "kubectl rollout status deployment/${service}-container -n ${KUBE_NAMESPACE} --timeout=120s"
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo '🧹 Limpiando el workspace...'
            cleanWs()
        }
        success {
            echo '¡Pipeline ejecutado exitosamente!'
        }
        failure {
            echo '¡El pipeline falló!'
        }
    }
}
