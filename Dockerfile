FROM java:7
COPY . /usr/src
WORKDIR /usr/src/peerstopeers/src
RUN javac test.java
CMD ["java", "test"]
