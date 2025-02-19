#!/bash/bin -e
env
JAVA_OPTS='-Xms3072M -Xmx3072M -Xmn1024M -Dserver.tomcat.max-threads=800 -Dhoe.service-group.missing.enable=true'
java $JAVA_OPTS -jar ./*.jar --spring.profiles.active=onpremise
