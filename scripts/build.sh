#!/bin/bash

chown memmaster:memmaster ./target/memmaster-server-1.0.0-SNAPSHOT.jar
chmod u+x ./target/memmaster-server-1.0.0-SNAPSHOT.jar
systemctl stop memmaster
sleep 3
cp ./target/memmaster-server-1.0.0-SNAPSHOT.jar /mnt/vol1/www/memmaster/memmaster-server.jar
chown memmaster:memmaster /mnt/vol1/www/memmaster/memmaster-server.jar
systemctl start memmaster