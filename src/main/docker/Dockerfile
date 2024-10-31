FROM --platform=linux/amd64 eclipse-temurin:21-alpine

WORKDIR /opt/app

COPY maven/conta.jar /opt/app
CMD ["java", "-jar", "conta.jar"]
EXPOSE 8028

