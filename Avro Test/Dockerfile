FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG jarFileName
ARG jarFileVersion
COPY build/libs/${jarFileName}-${jarFileVersion}.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]