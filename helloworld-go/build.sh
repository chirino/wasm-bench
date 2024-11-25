#!/bin/bash

set -e

resource_dir="../src/main/resources/org/graalvm/benchmark/helloworld"
mkdir -p ${resource_dir}

tinygo build -scheduler=none --no-debug \
  -o hello.wasm \
  -target=wasi -panic=trap -scheduler=none main.go

cp  hello.wasm ${resource_dir}/hello.wasm 
