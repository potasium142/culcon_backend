FROM openjdk:17-slim-bullseye

WORKDIR /workdir

COPY . .

RUN ./gradlew clean build --no-daemon  --parallel

EXPOSE 8080

# CMD ["bash"]
# ENTRYPOINT ["java","-jar","/workdir/app.jar"]
ENTRYPOINT [ "./gradlew" , "bootRun"]
