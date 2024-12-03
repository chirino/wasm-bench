#!/bin/sh

set -e

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

resource_dir="${SCRIPT_DIR}/../../src/main/resources/org/graalvm/benchmark/photon"
mkdir -p ${resource_dir}
cargo build --lib --release --target wasm32-unknown-unknown

cp ${SCRIPT_DIR}/target/wasm32-unknown-unknown/release/bench.wasm ${resource_dir}/.
cp ${SCRIPT_DIR}/src/test-image.png ${resource_dir}/.