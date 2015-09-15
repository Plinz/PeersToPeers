from debian 
run apt-get update && \
    apt-get install -y  openjdk-7-jdk && \
    apt-get clean 
workdir /srv/peerstopeers/
add src /srv/peerstopeers/src/
expose 5001
cmd javac /srv/peerstopeers/src/p2p/*.java
cmd cd /srv
cmd java peerstopeers.src.p2p.Rdv
