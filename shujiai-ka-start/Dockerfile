FROM registry.cn-hangzhou.aliyuncs.com/yundongcloud/yundong-base:1.0.0
EXPOSE 8080

USER admin:admin

ENV TZ 'Asia/Shanghai'
ENV LANG="C.UTF-8"

ARG JAR_FILE
ADD ${JAR_FILE} /home/admin/app.jar

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9099","-Dspring.profiles.active=onpremise","-jar","/home/admin/app.jar"]

