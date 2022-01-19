FROM openjdk:11 AS builder
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

FROM alpine:3.15
RUN apk --no-cache add openjdk11
COPY --from=builder build/libs/*.jar app.jar
# for debug
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=*:8000,server=y,suspend=n
EXPOSE 8000
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
