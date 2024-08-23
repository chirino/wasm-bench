package org.graalvm.benchmark;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.ByteSequence;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3)
@Measurement(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
public class GraalWasmTest extends WasmTest {

    @Benchmark
    public void graalWasmTest(WasmTestSetup setup) throws IOException {
        // Create context
        final Context.Builder contextBuilder = Context.newBuilder("wasm");
        contextBuilder.option("wasm.Builtins", "wasi_snapshot_preview1");
        try (Context context = contextBuilder.build()) {
            // Create source
            final Source.Builder sourceBuilder = Source.newBuilder("wasm", ByteSequence.create(setup.wasmBytes), "demo");
            final Source source = sourceBuilder.build();

            // instantiate and connect imports
            context.eval(source);
            final Value instance = context.getBindings("wasm").getMember("demo");

            // automatically exported by TinyGo
            final Value malloc = instance.getMember("malloc");
            final Value free = instance.getMember("free");

            final Value wasmFunc = instance.getMember(setup.wasmFunctionName);
            final Value memory = instance.getMember("memory");

            final String param = "Bob Morane";
            final byte[] data = param.getBytes();
            final int len = data.length;

            // allocate {len} bytes of memory, this returns a pointer to that memory
            final int ptr = malloc.execute(len).asInt();
            // write the message to the module's memory
            writeString(memory, ptr, data);

            // call the wasm function
            final Value result = wasmFunc.execute(ptr, len);
            // free input string memory
            free.executeVoid(ptr);

            // extract position and size from the result
            final int valuePosition = (int) ((result.asLong() >>> 32) & 0xFFFFFFFFL);
            final int valueSize = (int) (result.asLong() & 0xFFFFFFFFL);

            memory.readBuffer(valuePosition, new byte[valueSize], 0, valueSize);
        }
    }

    private void writeString(Value memory, int ptr, byte[] data) {
        for (int i = 0; i < data.length; i++) {
            memory.writeBufferByte(ptr + i, data[i]);
        }
    }
}