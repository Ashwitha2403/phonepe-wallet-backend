FROM eclipse-temurin:17-jdk
ENTRYPOINT ["java","-jar","/app/app.jar"]
WORKDIR /app
COPY target/phonepe-wallet-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]