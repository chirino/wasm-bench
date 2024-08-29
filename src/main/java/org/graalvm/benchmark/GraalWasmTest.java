package org.graalvm.benchmark;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
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
import org.openjdk.jmh.infra.Blackhole;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.ByteSequence;


@Warmup(iterations = 3)
@Measurement(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
public class GraalWasmTest {

    @State(Scope.Thread)
    public static class GraalWasmFixture extends WasmTestFixture {
        public Value instance;
        public Context context;

        @Setup(Level.Trial)
        public void doSetup() throws IOException {
            super.doSetup();
             final Context.Builder contextBuilder = Context.newBuilder("wasm");
            contextBuilder.option("wasm.Builtins", "wasi_snapshot_preview1");
            context = contextBuilder.build();
            final Source.Builder sourceBuilder = Source.newBuilder("wasm", ByteSequence.create(this.wasmBytes), "demo");
            final Source source = sourceBuilder.build();
            context.eval(source);
            instance = context.getBindings("wasm").getMember("demo");
        }

        @TearDown(Level.Trial)
        public void doTeardown() {
            context.close();
        }
    }

    @Benchmark
    public void graalWasmTest(GraalWasmFixture fixture, Blackhole blackhole) throws IOException {
        // automatically exported by TinyGo
        final Value malloc = fixture.instance.getMember("malloc");
        final Value free = fixture.instance.getMember("free");
        final Value wasmFunc = fixture.instance.getMember(fixture.wasmFunctionName);
        final Value memory = fixture.instance.getMember("memory");

        // allocate {fixture.paramLen} bytes of memory, this returns a pointer to that memory
        final int ptr = malloc.execute(fixture.paramLen).asInt();
        // write the message to the module's memory
        writeString(memory, ptr, fixture.paramBytes);

        // call the wasm function
        final Value result = wasmFunc.execute(ptr, fixture.paramLen);
        // free input string memory
        free.executeVoid(ptr);

        // extract position and size from the result
        final int valuePosition = (int) ((result.asLong() >>> 32) & 0xFFFFFFFFL);
        final int valueSize = (int) (result.asLong() & 0xFFFFFFFFL);

        // get byte[] of the result
        byte[] message = new byte[valueSize];
        memory.readBuffer(valuePosition, message, 0, valueSize);

        blackhole.consume(message);
    }

    private void writeString(Value memory, int ptr, byte[] data) {
        for (int i = 0; i < data.length; i++) {
            memory.writeBufferByte(ptr + i, data[i]);
        }
    }
}