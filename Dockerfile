FROM debian 
RUN apt-get update && \
    apt-get install -y  openjdk-7-jdk && \
    apt-get clean 
ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64
WORKDIR /srv/peerstopeers/src
ADD src /srv/peerstopeers/src/
EXPOSE 5001
CMD javac /srv/peerstopeers/src/test.java
CMD cd /srv/peerstopeers/src
CMD ls -la
CMD java "test"
