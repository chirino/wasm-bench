#!/bin/sh

set -e

cd helloworld-go
./clean.sh
cd ..

cd photon
./clean.sh
cd ..

./mvnw clean