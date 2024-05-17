# `docker image build --tag spring-boot-stack-web:v0.0.1 .`
# `docker container run --name spring-boot-stack-web --detach --restart always --publish 8080:8080 spring-boot-stack-web:v0.0.1`

# 使用官方的 Amazon Corretto 作为基础镜像
FROM amazoncorretto:21-al2-full
WORKDIR /opt
COPY build/libs/web-v?.?.?.jar app.jar
EXPOSE 8080
ENV TZ=Asia/Shanghai
# `host.docker.internal` 为宿主机网络
ENV SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/test_del
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=qwe@123
ENTRYPOINT ["java", "-jar", "app.jar"]
