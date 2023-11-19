FROM openjdk:17-alpine

RUN mkdir -p /src/usr/app
COPY . /src/usr/app
WORKDIR /src/usr/app

ADD build/libs/server-1.0-SNAPSHOT-all.jar server.jar

ENTRYPOINT java -jar server.jar -p ${PORT}