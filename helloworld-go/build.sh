#!/bin/bash

set -e

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

resource_dir="../src/main/resources/org/graalvm/benchmark/helloworld"
mkdir -p ${resource_dir}

docker run --rm \
    -v ${SCRIPT_DIR}/:/src \
    -w /src tinygo/tinygo:0.34.0 bash \
    -c "tinygo build --no-debug -target=wasm-unknown -panic=trap -scheduler=none -o /tmp/tmp.wasm main.go && cat /tmp/tmp.wasm" > \
    ${SCRIPT_DIR}/hello.wasm

cp  hello.wasm ${resource_dir}/hello.wasm 
