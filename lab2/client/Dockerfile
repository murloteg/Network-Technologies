FROM openjdk:17-alpine

RUN mkdir -p /src/usr/app
COPY . /src/usr/app
WORKDIR /src/usr/app

ADD build/libs/client-1.0-SNAPSHOT-all.jar client.jar

ENTRYPOINT java -jar client.jar -i ${IP_ADDR} -p ${PORT} -f ${FILE_PATH}