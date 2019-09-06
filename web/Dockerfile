FROM java:8-jdk-alpine

COPY ./build/libs/web-1.0.0.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch web-1.0.0.jar'

ENTRYPOINT ["java", "-jar", "web-1.0.0.jar"]