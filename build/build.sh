#!/bin/env bash
set -e

mvn clean package --file ../pom.xml 

cp ../target/abaqs-jar-with-dependencies.jar .

docker build --tag mycocosm/abaqs:latest .

rm abaqs-jar-with-dependencies.jar

# docker image push mycocosm/abaqs:latest