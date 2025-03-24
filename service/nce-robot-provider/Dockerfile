# 基础镜像
#FROM openjdk:8-jdk-alpine
FROM 10.136.30.167:10009/citc-nce/java_base:v2
VOLUME /tmp
ADD ./target/app.jar /app.jar
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
EXPOSE 8207
ENTRYPOINT exec java -Xms512M -Xmx512M -Xmn256M -Xss1M -XX:MetaspaceSize=256M -XX:MaxMetaspaceSize=512M    -jar   /app.jar