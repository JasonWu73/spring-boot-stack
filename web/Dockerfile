# `docker build -t spring-boot-stack-web:v0.0.1 .`
# `docker run --name spring-boot-stack-web -d --restart always -p 8088:8088 spring-boot-stack-web:v0.0.1`
# 使用官方的 Amazon Corretto 作为基础镜像
FROM amazoncorretto:21-al2-full
WORKDIR /app
COPY build/libs/web-v?.?.?.jar app.jar
EXPOSE 8088
ENV TZ=Asia/Shanghai
ENTRYPOINT ["java", "-jar", "app.jar"]
