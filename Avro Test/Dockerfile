FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG jarFileName
COPY ${jarFileName} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]