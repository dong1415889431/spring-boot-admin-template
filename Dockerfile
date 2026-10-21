# 多阶段构建：先编译，再打包成精简运行时镜像（第 5 周启用）
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# 支持通过构建参数注入 Maven 代理（默认空，直连用户不受影响）
# 示例：docker build --build-arg MAVEN_OPTS="-Dhttps.proxyHost=host -Dhttps.proxyPort=port"
ARG MAVEN_OPTS
ENV MAVEN_OPTS=${MAVEN_OPTS:-}
# 可选：指定 Maven 镜像源（如国内阿里云镜像），留空则用默认中央仓库
# 示例：docker build --build-arg MAVEN_MIRROR_URL=https://maven.aliyun.com/repository/public
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
