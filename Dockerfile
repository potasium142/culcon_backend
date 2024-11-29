FROM docker.io/openjdk:17-alpine

WORKDIR /workdir

COPY . .

#RUN ./gradlew build --no-daemon  --parallel
COPY build/libs/backend-0.0.1-SNAPSHOT.jar /workdir/app.jar

ARG DB_USERNAME=culcon
ARG DB_PASSWORD=culcon
ARG DB_URL_USER=jdbc:h2:file:./db/culcon_user;IFEXISTS=FALSE;
ARG DB_URL_RECORD=jdbc:h2:file:./db/culcon_record;IFEXISTS=FALSE;
ARG EMAIL_ADDRESS=culinaryconnect.777@gmail.com
ARG EMAIL_PASSWORD=ukzv ubhu doze buay

ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DB_URL_USER=${DB_URL_USER}
ENV DB_URL_RECORD=${DB_URL_RECORD}
ENV EMAIL_ADDRESS=${EMAIL_ADDRESS}
ENV EMAIL_PASSWORD=${EMAIL_PASSWORD}

EXPOSE 8080

ENTRYPOINT ["java","-jar","/workdir/app.jar"]
#ENTRYPOINT [ "./gradlew" , "bootRun"]
