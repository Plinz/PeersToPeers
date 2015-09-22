FROM java:7
ADD . /usr/peerstopeers
WORKDIR /usr/peerstopeers/src
RUN cd /usr/peerstopeers/src
RUN javac p2p/*.java
RUN ls
CMD ["java", "p2p.Rdv"]
EXPOSE 5001
