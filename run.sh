#!/bin/bash

# build fat jar with benchmarks
mvn clean package

# Run on GraalVM for JDK 21 with Graal JIT
java -jar target/benchmarks.jar

# Run with C2 (GraalVM) 
java \
    -Dpolyglot.engine.WarnInterpreterOnly=false \
    -Dtruffle.UseFallbackRuntime=true \
    -XX:-UseJVMCICompiler \
    -jar target/benchmarks.jar 

# Run with C2 (OpenJDK)
# java \
#     -Dpolyglot.engine.WarnInterpreterOnly=false \
#     -Dtruffle.UseFallbackRuntime=true \
#     -jar target/benchmarks.jar 
    