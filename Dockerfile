FROM java:7
ADD . /usr/peerstopeers
WORKDIR /usr/peerstopeers/src
RUN cd /usr/peerstopeers/src
RUN javac p2p/Rdv.java
RUN ls
EXPOSE 5001
CMD  -p 127.0.0.1::5000/udp ["java", "p2p.Rdv"]
