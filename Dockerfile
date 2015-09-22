FROM java:7
ADD . /usr/peerstopeers
WORKDIR /usr/peerstopeers/src
RUN cd /usr/peerstopeers/src
RUN javac *.java
RUN ls
CMD ["java", "Rdv"]
EXPOSE 5001
