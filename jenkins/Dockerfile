FROM jenkins/jenkins:lts

USER root

# Instalar Docker CLI
RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get install -y maven && \
    apt-get install -y python3 python3-pip && \
    pip3 install locust --break-system-packages && \
    usermod -aG docker jenkins


USER jenkins
