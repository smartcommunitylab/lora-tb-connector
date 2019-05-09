FROM maven:3.3-jdk-8 AS mvn
COPY . /tmp
WORKDIR /tmp
RUN mvn install

FROM adoptopenjdk/openjdk8:alpine
ARG VER=0.1
ENV FOLDER=/tmp/target
ENV APP=lora-tb-connector-${VER}.jar
RUN apk update && apk add curl && rm -rf /var/cache/apk/*
COPY --from=mvn ${FOLDER}/${APP} /tmp/target/
WORKDIR /tmp/target/
CMD [ "sh", "-c", "java -jar ${APP}" ]
