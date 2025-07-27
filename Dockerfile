FROM eclipse-temurin:24-jdk-alpine AS build

WORKDIR /app
COPY ./  /app

RUN ./gradlew build -x check --no-daemon

FROM eclipse-temurin:24-jre-alpine
# maybe using 'jdk' can be beneficial at some specific moments

RUN apk update

COPY --from=build /app/build/libs/order-processor-0.0.1-SNAPSHOT.jar app.jar
RUN adduser appuser -D && \
    chmod +rx app.jar

USER appuser

ENTRYPOINT ["exec", "java", "$JAVA_OPTS", "-jar", "app.jar"]
