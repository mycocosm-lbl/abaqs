# syntax=docker/dockerfile:1
FROM ubuntu:latest

LABEL maintainer="ishabalov@lbl.gov"

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y default-jdk
RUN java -version

ENV JAVA_HOME="/usr/lib/jvm/default-java"

RUN mkdir /abaqs

WORKDIR /abaqs
COPY abaqs-jar-with-dependencies.jar .

ENV ABAQS_HOME="/abaqs"

ENV PATH="${ABAQS_HOME}:${JAVA_HOME}/bin:${PATH}"

ENTRYPOINT ["java", "-jar", "/abaqs/abaqs-jar-with-dependencies.jar"]