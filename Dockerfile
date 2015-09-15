from debian 
run apt-get update && \
    apt-get install -y  openjdk-7-jdk && \
    apt-get clean 
workdir /srv/peerstopeers/
add src /srv/peerstopeers/src/
expose 5001
cmd javac /srv/peerstopeers/src/p2p/*.java
cmd cd /srv/peerstopeers/src/
cmd java p2p.Rdv 178.32.240.91 5000
