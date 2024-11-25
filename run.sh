#!/bin/bash

# build fat jar with benchmarks
./mvnw clean verify

# Configure SDKMAN!
# NOTE: turn off interactive mode, see https://sdkman.io/usage#configuration
. ${HOME}/.sdkman/bin/sdkman-init.sh

# Run on Oracle GraalVM with Oracle Graal JIT
sdk install java 23.0.1-graal
sdk use java 23.0.1-graal
java -jar target/benchmarks.jar

# Run on GraalVM CE with Graal CE JIT
sdk install java 23.0.1-graalce
sdk use java 23.0.1-graalce
java -jar target/benchmarks.jar

# Run on Eclipse Temurin with C2 JIT
sdk install java 23.0.1-tem
sdk use java 23.0.1-tem
java -jar target/benchmarks.jar
   