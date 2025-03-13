FROM docker.io/alpine/java:21-jdk

WORKDIR /workdir

COPY . .

#RUN ./gradlew build --no-daemon  --parallel
COPY build/libs/backend-0.0.1-SNAPSHOT.jar /workdir/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/workdir/app.jar"]
#ENTRYPOINT [ "./gradlew" , "bootRun"]
