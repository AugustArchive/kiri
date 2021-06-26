# Why not use the official builds?
# AdoptOpenJDK only allow Alpine builds available under the `adoptopenjdk` image.
# TODO: maybe do it as noelware/adoptopenjdk:16?

FROM alpine:latest

RUN apk add --no-cache git curl ca-certificates && rm -rf /var/cache/apk/*
RUN mkdir -p /opt/java/adoptopenjdk
RUN curl -X GET -L -o /tmp/adoptopenjdk.tar.gz https://github.com/AdoptOpenJDK/openjdk16-binaries/releases/download/jdk-16.0.1%2B9/OpenJDK16U-jdk_x64_alpine-linux_hotspot_16.0.1_9.tar.gz
RUN tar -xvf /tmp/adoptopenjdk.tar.gz -C /opt/java/adoptopenjdk
RUN rm /tmp/adoptopenjdk.tar.gz
ENV JAVA_HOME="/opt/java/adoptopenjdk/jdk-16.0.1+9" \
    PATH="/opt/java/adoptopenjdk/bin:$PATH"

WORKDIR /opt/kiri
COPY . .

RUN chmod +x gradlew
RUN ./gradlew build

ENTRYPOINT [ "java", "-jar", "./build/libs/Kiri-master.jar" ]
