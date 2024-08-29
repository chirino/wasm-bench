package org.graalvm.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class WasmTestFixture {
    public byte[] wasmBytes;
    public String wasmFunctionName;
    public String param;
    public byte[] paramBytes;
    public int paramLen;

    public void doSetup() throws IOException {
        var wasmFileLocalLocation = Optional.ofNullable(System.getenv("WASM_FILE")).orElse("./demo-plugin/demo.wasm");
        wasmFunctionName = Optional.ofNullable(System.getenv("FUNCTION_NAME")).orElse("hello");

        // Load wasm file into byte[]
        final Path filePath = Paths.get(wasmFileLocalLocation);
        wasmBytes = Files.readAllBytes(filePath);

        // Test parameters
        param = "World";
        paramBytes = param.getBytes();
        paramLen = paramBytes.length;
    }
}