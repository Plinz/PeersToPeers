FROM java:7
RUN ls
ADD . /usr/peerstopeers
WORKDIR /usr/peerstopeers/src
RUN cd /usr/peerstopeers/src
RUN ls
RUN javac test.java
CMD ["java", "test"]
