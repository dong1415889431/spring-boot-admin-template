# Multi-stage build: compile first, then package into a slim runtime image (enabled in Week 5)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Supports injecting a Maven proxy via build args (empty by default; direct-connect users are unaffected)
# Example: docker build --build-arg MAVEN_OPTS="-Dhttps.proxyHost=host -Dhttps.proxyPort=port"
ARG MAVEN_OPTS
ENV MAVEN_OPTS=${MAVEN_OPTS:-}
# Optional: specify a Maven mirror (e.g., the Alibaba Cloud mirror in China); leave empty to use the default central repository
# Example: docker build --build-arg MAVEN_MIRROR_URL=https://maven.aliyun.com/repository/public
ARG MAVEN_MIRROR_URL
RUN if [ -n "$MAVEN_MIRROR_URL" ]; then \
      mkdir -p /root/.m2 && printf '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"><mirrors><mirror><id>mirror</id><mirrorOf>central</mirrorOf><url>%s</url></mirror></mirrors></settings>' "$MAVEN_MIRROR_URL" > /root/.m2/settings.xml; \
    fi
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
