FROM java:7
ADD . /usr/peerstopeers
WORKDIR /usr/peerstopeers/src
RUN cd /usr/peerstopeers/src
RUN javac *.java
CMD ["java", "p2p.Rdv"]
EXPOSE 5001
