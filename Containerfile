FROM openjdk:17-slim-bullseye

WORKDIR /workdir

# ENV DB_URL=jdbc:postgresql://sussywussy:5432/culcon_user

COPY . .

# RUN ./gradlew dependencies

RUN ./gradlew clean build --no-daemon  --parallel

# ARG JAR_FILE=./build/libs/backend-0.0.1-SNAPSHOT.jar

# COPY ${JAR_FILE} ./app.jar

EXPOSE 8080

# CMD ["bash"]
# ENTRYPOINT ["java","-jar","/workdir/app.jar"]
ENTRYPOINT [ "./gradlew" , "bootRun"]
