# Stage 1: Build da aplicação com Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Baixa as dependencias offline para acelerar builds subsequentes
RUN mvn dependency:go-offline -B
COPY src ./src
# Realiza o build ignorando os testes (o Render pode dar timeout se testes demorarem)
RUN mvn clean package -DskipTests

# Stage 2: Imagem final para rodar a aplicacao (apenas o JRE, mais leve)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
