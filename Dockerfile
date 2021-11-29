FROM openjdk:8-jre

#################################################################
#Masscan 1.3.0 install.

RUN apt update
RUN apt install -y make gcc libpcap-dev

RUN mkdir /tmp/masscan_install
WORKDIR /tmp/masscan_install
RUN wget https://github.com/robertdavidgraham/masscan/archive/1.3.2.tar.gz
RUN tar xvzf 1.3.2.tar.gz
WORKDIR /tmp/masscan_install/masscan-1.3.2
RUN make install
RUN rm -rf /tmp/masscan_install
WORKDIR /
#################################################################

#Copy the war.
ARG JAR_FILE=target/app.jar
COPY ${JAR_FILE} app.jar

#Setup the default log4j2.conf.
ARG LOG4J2_FILE=src/main/resources/log4j2-spring.xml
COPY ${LOG4J2_FILE} /conf/log4j2.xml

CMD ["java", "-Dlogging.config=/conf/log4j2.xml","-jar","/app.jar"]
