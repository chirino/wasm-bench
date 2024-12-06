#!/bin/sh

set -e

cd helloworld-go
./build.sh
cd ..

cd photon
./build.sh
cd ..

./mvnw clean package
