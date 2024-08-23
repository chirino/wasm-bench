package org.graalvm.benchmark;

import com.dylibso.chicory.log.SystemLogger;
import com.dylibso.chicory.runtime.ExportFunction;
import com.dylibso.chicory.runtime.Module;
import com.dylibso.chicory.wasi.WasiOptions;
import com.dylibso.chicory.wasm.types.Value;
import com.dylibso.chicory.runtime.Memory;
import com.dylibso.chicory.runtime.HostImports;
import com.dylibso.chicory.wasi.WasiPreview1;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@Warmup(iterations = 3)
@Measurement(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
public class ChicoryTest extends WasmTest {
    
    @Benchmark
    public void chicoryTest(WasmTestSetup setup, Blackhole blackhole) throws IOException {
        final var logger = new SystemLogger();
        // create our instance of wasip1
        try (final var wasi = new WasiPreview1(logger, WasiOptions.builder().build())) {
            final var imports = new HostImports(wasi.toHostFunctions());
            // create the module
            final var module = Module.builder(setup.wasmBytes).withHostImports(imports).build();
            // instantiate (the module) and connect our imports
            final var instance = module.instantiate();
        
            // automatically exported by TinyGo
            final ExportFunction malloc = instance.export("malloc");
            final ExportFunction free = instance.export("free");

            final ExportFunction wasmFunc = instance.export(setup.wasmFunctionName);
            final Memory memory = instance.memory();

            final var param = "Bob Morane";
            final int len = param.getBytes().length;

            // allocate {len} bytes of memory, this returns a pointer to that memory
            final int ptr = malloc.apply(Value.i32(len))[0].asInt();
            // We can now write the message to the module's memory:
            memory.writeString(ptr, param);

            // Call the wasm function
            final Value result = wasmFunc.apply(Value.i32(ptr), Value.i32(len))[0];
            // free input string memory
            free.apply(Value.i32(ptr), Value.i32(len));

            // Extract position and size from the result
            final int valuePosition = (int) ((result.asLong() >>> 32) & 0xFFFFFFFFL);
            final int valueSize = (int) (result.asLong() & 0xFFFFFFFFL);

            blackhole.consume(memory.readBytes(valuePosition, valueSize));   
        }
    }
}