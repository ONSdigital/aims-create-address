FROM openjdk:17-oracle
COPY build/libs/*.jar temp/
COPY temp app.jar
