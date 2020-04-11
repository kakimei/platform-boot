FROM tomcat:jdk8

#RUN apk-install curl

#ENV JVM_MEMORY -Xms1g -Xmx1g -XX:PermSize=256m -XX:MaxPermSize=256m
ENV SPRING_PROFILE my

ADD setenv.sh ${CATALINA_HOME}/bin/setenv.sh

COPY target/*.war "${CATALINA_HOME}"/webapps/ROOT.war

EXPOSE 8081

VOLUME ["/boot/logs/platform-boot"]