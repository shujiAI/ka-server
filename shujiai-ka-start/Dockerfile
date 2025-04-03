FROM moan-docker.tencentcloudcr.com/base/openjdk8:v1
EXPOSE 8080

ENV TZ='Asia/Shanghai'
ENV LANG="C.UTF-8"

ARG JAR_FILE
ADD ${JAR_FILE} /home/admin/kaserver.jar

ENTRYPOINT ["java", "-Xms2048m","-Xmx2048m","-Xmn512m","-XX:MetaspaceSize=256m","-XX:MaxMetaspaceSize=384m","-Xss512k","-XX:MaxDirectMemorySize=256m","-jar","/home/admin/kaserver.jar", "--spring.profiles.active=onpremise"]
