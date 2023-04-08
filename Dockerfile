FROM openjdk:8
EXPOSE 8080
ADD target/com.amazon.automation-0.0.1-SNAPSHOT.jar com.amazon.automation-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/com.amazon.automation-0.0.1-SNAPSHOT.jar"]