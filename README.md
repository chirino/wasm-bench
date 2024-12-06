# About

This simplistic benchmark suite uses [JMH](https://github.com/openjdk/jmh) to
compare the performance of a simple Go HelloWorld app and Rust
[Photon](https://github.com/silvia-odwyer/photon) image processing app compiled
to [WebAssembly](https://webassembly.org/) and run on the JVM wth both
[GraalWasm](https://www.graalvm.org/webassembly/) and
[Chicory](https://chicory.dev/). Three JIT compilers from three different JDK
distro are used: Oracle [GraalVM](www.graalvm.org) JIT, GraalVM Community
Edition JIT, and the default OpenJDK C2 JIT.

GraalWasm leverages the Graal JIT (Oracle or Community), when available, to compile
applications into native machine code. When the Graal JIT is not available GraalWasm
fall back to the interpreter. 

Chicory provides an interpreter and experimental ahead-of-time compilation
from WebAssembly to Java Bytecode.

## Acknowlegements

The Go benchmark is based on code from:
* https://github.com/world-wide-wasm/chicory-starter-kit

# Setup

## SDKMAN!

[SDKMAN!](https://sdkman.io/) is the easiest way to install JDKs and it is used
to install and select a JDK for each run of the benchmark suite.

You'll need to install it and configure it for non-interactive mode.  This will
avoid it asking for action confirmations when run from a script.  Once
installed, edit ~/.skdman/etc/config to set the following:
```sh
sdkman_auto_answer=true
```

## Go/TinyGo

You need Docker installed as the build uses a TinyGo container to compile Go to WASM.

## Install Rust

* See https://www.rust-lang.org/tools/install

# Build

To compile the Go and Rust apps, and the Java benchmarks, run the top level `buildall.sh` script.

**NOTE**: You may have to run `buildall.sh` twice as there's an as yet unresolved
interaction issue with the Chicory AOT Maven plugin and `javac`.

# Run

In the project root directory run `run.sh` to run all benchmarks. 

This will run all the benchmarks in the suite three times, once on Oracle
GraalVM, once on GraalVM Community Edition, and once on Eclipse Temurin (with
Truffle JIT enabled for GraalWasm compilation support). An additional GraalWasm
benchmark is run on Eclipse Temurin with Truffle JIT compilation disabled to
force the use of the GraalWasm interpreter.
