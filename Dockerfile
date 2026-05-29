FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/chatbot-ai-platform-1.0-SNAPSHOT.jar app.jar

EXPOSE 10000

CMD ["java", "-jar", "app.jar"]