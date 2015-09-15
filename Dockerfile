FROM debian 
RUN apt-get update && \
    apt-get install -y  openjdk-7-jdk && \
    apt-get clean 
ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64
WORKDIR /srv/peerstopeers/src
ADD https://github.com/Plinz/peerstopeers/src /srv/peerstopeers/src/
EXPOSE 5001
CMD javac test.java
CMD java test
