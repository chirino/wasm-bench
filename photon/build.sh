#!/bin/sh

set -ex

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

resource_dir="${SCRIPT_DIR}/../src/main/resources/org/graalvm/benchmark/photon"
mkdir -p ${resource_dir}

docker run --rm \
    -v ${SCRIPT_DIR}/:/photon:Z \
    -w /photon/bench \
    rust:1.83 \
    bash -c "rustup target add wasm32-unknown-unknown; cargo build --lib --release --target wasm32-unknown-unknown"

cp ${SCRIPT_DIR}/bench/target/wasm32-unknown-unknown/release/bench.wasm ${resource_dir}/.
cp ${SCRIPT_DIR}/bench/src/test-image.png ${resource_dir}/.

