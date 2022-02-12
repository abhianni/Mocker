#FROM centos:centos6
FROM 524881529748.dkr.ecr.ap-south-1.amazonaws.com/flights-ecs-base:java8
MAINTAINER harpal.lodha@go-mmt.com

#RUN rpm -ivh http://dl.fedoraproject.org/pub/epel/6/x86_64/epel-release-6-8.noarch.rpm

#RUN yum update -y

RUN yum -y install vim logcheck python-pip

RUN cd /etc/ && rm localtime && ln -s /usr/share/zoneinfo/Asia/Kolkata localtime

RUN mkdir -p /opt/wiremock-service/

COPY ./wiremock/target/wiremock-0.0.1-SNAPSHOT.jar /opt/wiremock-service/wiremock-service-0.0.1-SNAPSHOT.jar

RUN mkdir -p /opt/logs/
RUN chmod -R 777 /opt/logs

RUN mkdir -p /opt/wiremock-service/src/
COPY ./wiremock/src/ /opt/wiremock-service/src/

COPY ./docker/startwiremock.sh /opt/wiremock-service/

RUN chmod -R 777 /opt/wiremock-service
# install Java
RUN yum install java-1.8.0-openjdk -y

# nginx configurations #Install nginx
RUN yum install epel-release -y
RUN yum install nginx -y
RUN mkdir -p /opt/nginx/logs/
COPY ./docker/nginx.conf /etc/nginx/

#To copy Agent Jar
#RUN mkdir -p /opt/jacoco/lib/
#COPY ./docker/jacocoagent.jar /opt/jacoco/lib/

# Hosts file to edit
COPY ./docker/hosts /tmp/

# install supervisord
RUN pip install meld3==0.6.7 supervisor==3.0.0
COPY ./docker/supervisord.conf /etc/supervisord.conf

#CMD ["/usr/bin/supervisord"]
ENTRYPOINT ["/usr/bin/supervisord"]
