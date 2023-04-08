FROM ubuntu:20.04
# Install packages, dependencies and remove temp files
RUN apt-get update && \
    apt-get -y install maven && \
    apt-get -y install default-jdk && \
    apt-get -y install default-jre && \
    apt-get -y install libxss1 libappindicator1 libindicator7 && \
    apt-get -y install firefox && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
# Set working directory
WORKDIR /analyticsportal-apiautomation-docker
# Copy source code and configuration files
COPY src src
COPY pom.xml .
COPY Suite.xml .
# Build package
RUN mvn package