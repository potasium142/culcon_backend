# Build stage
FROM docker.io/gradle:8.10.2-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test --parallel

# Runtime stage
FROM docker.io/alpine/java:21-jre
WORKDIR /workdir
COPY --from=build /app/build/libs/backend-0.0.1-SNAPSHOT.jar /workdir/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/workdir/app.jar"]
