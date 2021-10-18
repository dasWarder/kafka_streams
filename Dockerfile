FROM maven:3.8.3-openjdk-11-slim
COPY ./ ./home/mvn
RUN mvn clean install -Dmaven.test.skip=true

FROM openjdk:11-jdk-slim
ARG PACKAGE=/target/*.jar
COPY $PACKAGE /kafka_stream.jar

ENTRYPOINT ["java", "-jar", "/kafka_stream.jar"]