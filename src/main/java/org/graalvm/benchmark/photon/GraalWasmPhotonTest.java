/*
 * Copyright (c) 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at https://opensource.org/license/UPL.
 */

 package org.graalvm.benchmark.photon;

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


@Warmup(iterations = 3)
@Measurement(iterations = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
public class GraalWasmPhotonTest {

    @State(Scope.Benchmark)
    public static class GraalWasmFixture {
        private Context context;
        public Value benchmarkFn;

        @Setup(Level.Trial)
        public void doSetup() throws IOException {
            final Context context = Context.newBuilder("wasm")
                .option("wasm.Builtins", "wasi_snapshot_preview1")
                .build();
            final Source source = Source.newBuilder("wasm", GraalWasmFixture.class.getResource(PhotonTestParams.WASM_FILENAME))
                .name("photon")
                .build();
            context.eval(source);
            Value benchmark = context.getBindings("wasm").getMember("photon");
            benchmarkFn = benchmark.getMember(PhotonTestParams.WASM_FUNCTION);
        }

        @TearDown(Level.Trial)
        public void doTeardown() {
            if (null != context) {
                context.close();
            }
        }
    }

    @Benchmark
    public void graalWasmTest(GraalWasmFixture fixture, Blackhole blackhole) throws IOException {
        final int hash = fixture.benchmarkFn.execute().asInt();
        blackhole.consume(hash);
    }
}