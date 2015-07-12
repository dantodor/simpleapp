#!/bin/bash
cd ./tester
mvn clean package 
cd ../simpleapp
mvn -DskipTests=true clean package 
cd ..
rm -rf deploy
mkdir deploy
mkdir deploy/tester
mkdir deploy/simpleapp

mv ./simpleapp/target/simpleapp-1.0-SNAPSHOT-allinone.jar ./deploy/simpleapp/simpleapp.jar
cp ./simpleapp/run.sh ./deploy/simpleapp
cp ./simpleapp/logback.xml ./deploy/simpleapp
cp ./simpleapp/app.conf ./deploy/simpleapp

mv ./tester/target/tester-1.0-SNAPSHOT-allinone.jar ./deploy/tester/tester.jar
cp ./tester/run.sh ./deploy/tester
cp ./tester/logback.xml ./deploy/tester
cp ./tester/tester.conf ./deploy/tester

