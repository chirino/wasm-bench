#!/bin/sh

set -e

resource_dir="../../src/main/resources/org/graalvm/benchmark/photon"
mkdir -p ${resource_dir}
cargo build --lib --release --target wasm32-unknown-unknown

cp target/wasm32-unknown-unknown/release/bench.wasm ${resource_dir}/.
cp src/test-image.png ${resource_dir}/.