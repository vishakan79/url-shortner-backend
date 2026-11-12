FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY url-shortener-sb/pom.xml .
RUN mvn -q -B dependency:go-offline
COPY url-shortener-sb/src ./src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS="-XX:+UseSerialGC -Xmx380m -Xss512k -XX:TieredStopAtLevel=1"
EXPOSE 8080
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
