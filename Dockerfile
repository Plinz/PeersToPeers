FROM debian 
RUN apt-get update && \
    apt-get install -y openjdk-7-jdk && \
    apt-get install -y openjdk-7-jre && \
    apt-get clean 
ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64
COPY . /srv
WORKDIR /srv/peerstopeers/src
EXPOSE 5001
RUN javac /srv/peerstopeers/src/p2p/*.java
CMD ["java"], "p2p.Rdv"]
