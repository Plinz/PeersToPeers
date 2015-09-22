FROM java:7
ADD . /usr/peerstopeers
WORKDIR /usr/src/peerstopeers/src
RUN javac test.java
CMD ["java", "test"]
