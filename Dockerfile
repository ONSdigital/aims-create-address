FROM openjdk:17-oracle
COPY build/libs/*.jar app/
COPY app/ app.jar
ENTRYPOINT ["java","-jar","/app.jar"]