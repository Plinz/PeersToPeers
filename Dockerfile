FROM java:7
COPY . /usr/src
WORKDIR /usr/src/peerstopeers/src
RUN javac *.java
CMD ["java", "p2p.Rdv"]
