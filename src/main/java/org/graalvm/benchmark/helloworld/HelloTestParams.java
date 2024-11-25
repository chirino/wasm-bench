/*
 * Copyright (c) 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at https://opensource.org/license/UPL.
 */

package org.graalvm.benchmark.helloworld;

class HelloTestParams {
    protected static final String TEST_PARAM = "World";
    protected static final String WASM_FUNCTION = "hello";
    protected static final String WASM_FILENAME = "hello.wasm";
    protected static final byte[] paramBytes;
    protected static final int paramLen;

    static {
        paramBytes = TEST_PARAM.getBytes();
        paramLen = paramBytes.length;
    }
   
}