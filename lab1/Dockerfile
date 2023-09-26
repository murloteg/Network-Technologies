FROM openjdk:17-alpine

RUN mkdir -p /usr/src/app/
COPY . /usr/src/app/
WORKDIR /usr/src/app/

ADD /target/lab1-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

ENTRYPOINT java -jar app.jar -p ${PORT:-8081} -i ${IP_ADDR:-225.225.225.225}