from debian 
run apt-get update && \
    apt-get install -y  openjdk-7-jdk && \
    apt-get clean 
workdir /srv/PeersToPeers/
add src /srv/PeersToPeers/src/
expose 5001
cmd javac *.java; java p2p.Rdv
