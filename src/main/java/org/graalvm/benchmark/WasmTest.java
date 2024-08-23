package org.graalvm.benchmark;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3)
@Measurement(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
public class WasmTest {

     @State(Scope.Thread)
     public static class WasmTestSetup {
        public byte[] wasmBytes;
        public String wasmFunctionName;

        @Setup(Level.Trial)
        public void doSetup() throws IOException {
            var wasmFileLocalLocation = Optional.ofNullable(System.getenv("WASM_FILE")).orElse("./demo-plugin/demo.wasm");
            wasmFunctionName = Optional.ofNullable(System.getenv("FUNCTION_NAME")).orElse("hello");
    
            // Load file
            final Path filePath = Paths.get(wasmFileLocalLocation);
            wasmBytes = Files.readAllBytes(filePath);
        }
    }
}