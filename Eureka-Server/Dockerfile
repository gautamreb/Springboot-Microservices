FROM openjdk:8-jdk-alpine

# Adding a /tmp volume. Docker will map this to /var/lib/docker on the host system. 
# This is the directory Spring Boot will configure Tomcat to use as its working directory.
VOLUME /tmp

RUN apk add dos2unix

# Adding a folder for EFS shared file system
RUN mkdir /tmpEFS && chmod 777 /tmpEFS

ADD target/Eureka-Server.war app.war

COPY docker-resources/entry_point.sh /entry_point.sh
RUN chmod 777 /entry_point.sh
RUN dos2unix /entry_point.sh

ENTRYPOINT ["sh","/entry_point.sh"]