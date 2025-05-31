pipeline {
    agent any

    environment {
        DOCKERHUB_USERNAME = 'juansmc'
        DOCKER_CREDENTIALS_ID = 'dockerhub-creds'
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
                    def isProd = env.BRANCH_NAME == 'master'
                    def isStage = env.BRANCH_NAME == 'stage'
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
                                if (isStage || isProd) {
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
                    def tag = (env.BRANCH_NAME == 'master') ? "prod-${env.BUILD_NUMBER}" : "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
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
                                echo "Construyendo imagen Docker para ${service}..."
                                dir(service) {
                                    def image = docker.build("${imageName}:${tag}")
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
                    def tag = (env.BRANCH_NAME == 'master') ? "prod-${env.BUILD_NUMBER}" : "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
                    def SERVICES = [
                        'api-gateway', 'user-service', 'cloud-config', 'order-service',
                        'payment-service', 'shipping-service', 'product-service',
                        'service-discovery', 'favourite-service'
                    ]

                    SERVICES.each { service ->
                        def deploymentFile = "${env.WORKSPACE}/${env.KUBE_MANIFESTS_DIR}/${service}/${service}-container-deployment.yaml"
                        def imageName = "${env.DOCKERHUB_USERNAME}/${service}-ecommerce-boot"
                        if (fileExists(deploymentFile)) {
                            echo "Actualizando manifiesto de ${service}..."
                            sh "sed -i 's|image: .*|image: ${imageName}:${tag}|g' ${deploymentFile}"
                        }
                    }
                }
            }
        }

        stage('4.5 Crear Namespace si no existe') {
            steps {
                script {
                    def kubeNamespace = (env.BRANCH_NAME == 'master') ? 'prod' :
                                        (env.BRANCH_NAME == 'stage') ? 'stage' :
                                        env.BRANCH_NAME

                    def output = sh(script: "kubectl get ns ${kubeNamespace} --ignore-not-found", returnStdout: true).trim()

                    if (output) {
                        echo "Namespace '${kubeNamespace}' ya existe."
                    } else {
                        echo "Namespace '${kubeNamespace}' no existe. Creándolo..."
                        sh "kubectl create namespace ${kubeNamespace}"
                    }

                    // Setear namespace como variable de entorno para próximas etapas
                    env.KUBE_NAMESPACE = kubeNamespace
                }
            }
        }

        stage('5. Deploy a Kubernetes') {
            steps {
                script {
                    def tag = (env.BRANCH_NAME == 'master') ? "prod-${env.BUILD_NUMBER}" : "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
                    def kubeNamespace = env.KUBE_NAMESPACE
                    def SERVICES = [
                        'api-gateway', 'user-service', 'cloud-config', 'order-service',
                        'payment-service', 'shipping-service', 'product-service',
                        'service-discovery', 'favourite-service'
                    ]

                    SERVICES.each { service ->
                        def svcFile = "${KUBE_MANIFESTS_DIR}/${service}/${service}-container-service.yaml"
                        def depFile = "${KUBE_MANIFESTS_DIR}/${service}/${service}-container-deployment.yaml"

                        echo "Desplegando ${service} en namespace '${kubeNamespace}'..."
                        sh "kubectl apply -f ${svcFile} -n ${kubeNamespace}"
                        sh "kubectl apply -f ${depFile} -n ${kubeNamespace}"
                        sh "kubectl rollout status deployment/${service}-container -n ${kubeNamespace} --timeout=120s"
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
