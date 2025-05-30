pipeline {
    agent any

    environment {
        DOCKERHUB_USERNAME = 'juansmc'
        DOCKER_CREDENTIALS_ID = 'dockerhub-creds'
        KUBE_NAMESPACE = 'development'
        KUBE_MANIFESTS_DIR = 'k8s'
    }

    stages {
        stage('1. Checkout') {
            steps {
                echo "Clonando el repositorio..."
                checkout scm
                echo "Checkout completado para la rama: ${env.BRANCH_NAME}"
            }
        }

        stage('2. Build & Test') {
            steps {
                script {
                    def SERVICES = [
                        'api-gateway', 'user-service', 'cloud-config', 'order-service',
                        'payment-service', 'shipping-service', 'product-service',
                        'service-discovery', 'favourite-service'
                    ]

                    SERVICES.each { service ->
                        def buildPath = "${service}/pom.xml"
                        if (fileExists(buildPath)) {
                            echo "Compilando y testeando ${service}..."
                            dir(service) {
                                if (env.BRANCH_NAME == 'stage') {
                                    sh 'mvn clean verify'
                                } else {
                                    sh 'mvn clean package -DskipTests'
                                }
                            }
                        } else {
                            echo "${service} no tiene pom.xml, omitiendo..."
                        }
                    }
                }
            }
        }

        stage('3. Docker Build & Push') {
            steps {
                script {
                    def SERVICES = [
                        'api-gateway', 'user-service', 'cloud-config', 'order-service',
                        'payment-service', 'shipping-service', 'product-service',
                        'service-discovery', 'favourite-service'
                    ]

                    docker.withRegistry('https://registry.hub.docker.com', env.DOCKER_CREDENTIALS_ID) {
                        SERVICES.each { service ->
                            def buildPath = "${service}/pom.xml"
                            if (fileExists(buildPath)) {
                                def imageName = "${env.DOCKERHUB_USERNAME}/${service}-ecommerce-boot"
                                def imageTag = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"

                                echo "Construyendo imagen Docker para ${service}..."
                                dir(service) {
                                    def image = docker.build("${imageName}:${imageTag}")
                                    image.push()
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('4. Actualizar manifiestos de Kubernetes') {
            steps {
                script {
                    def SERVICES = [
                        'api-gateway', 'user-service', 'cloud-config', 'order-service',
                        'payment-service', 'shipping-service', 'product-service',
                        'service-discovery', 'favourite-service'
                    ]

                    SERVICES.each { service ->
                        def deploymentFile = "${env.WORKSPACE}/${env.KUBE_MANIFESTS_DIR}/${service}/${service}-container-deployment.yaml"
                        def imageName = "${env.DOCKERHUB_USERNAME}/${service}-ecommerce-boot"
                        def imageTag = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"

                        if (fileExists(deploymentFile)) {
                            echo "Actualizando manifiesto de ${service}..."
                            sh "sed -i 's|image: .*|image: ${imageName}:${imageTag}|g' ${deploymentFile}"
                            sh "cat ${deploymentFile}" 
                        }
                    }
                }
            }
        }
        
        stage('4.5 Crear Namespace si no existe') {
            steps {
                script {
                    def ns = env.KUBE_NAMESPACE
                    def output = sh(script: "kubectl get ns ${ns} --ignore-not-found", returnStdout: true).trim()

                    if (output) {
                        echo "Namespace '${ns}' ya existe."
                    } else {
                        echo "Namespace '${ns}' no existe. Creándolo..."
                        sh "kubectl create namespace ${ns}"
                    }
                }
            }
        }


        stage('5. Deploy a Kubernetes') {
            steps {
                script {
                    def SERVICES = [
                        'api-gateway', 'user-service', 'cloud-config', 'order-service',
                        'payment-service', 'shipping-service', 'product-service',
                        'service-discovery', 'favourite-service'
                    ]

                    SERVICES.each { service ->
                        sh "ls -la ${KUBE_MANIFESTS_DIR}/${service}"
                        def svcFile = "${KUBE_MANIFESTS_DIR}/${service}/${service}-service.yaml"
                        def depFile = "${KUBE_MANIFESTS_DIR}/${service}/${service}-container-deployment.yaml"
                
                        echo "✔ Archivos encontrados para ${service}"
                        sh "cat ${svcFile}"
                        sh "cat ${depFile}"

                        echo "🚀 Desplegando ${service}..."
                        sh "kubectl apply -f ${svcFile} -n ${KUBE_NAMESPACE}"
                        sh "kubectl apply -f ${depFile} -n ${KUBE_NAMESPACE}"
                        sh "kubectl rollout status deployment/${service}-container -n ${KUBE_NAMESPACE} --timeout=120s"
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Limpiando el workspace...'
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
