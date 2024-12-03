// Package main
package main

import "unsafe"

// alloc/free implementation from:
// https://github.com/tinygo-org/tinygo/blob/2a76ceb7dd5ea5a834ec470b724882564d9681b3/src/runtime/arch_tinygowasm_malloc.go#L7
var allocs = make(map[uintptr][]byte)

//export malloc
func libc_malloc(size uintptr) unsafe.Pointer {
	if size == 0 {
		return nil
	}
	buf := make([]byte, size)
	ptr := unsafe.Pointer(&buf[0])
	allocs[uintptr(ptr)] = buf
	return ptr
}

//export free
func libc_free(ptr unsafe.Pointer) {
	if ptr == nil {
		return
	}
	if _, ok := allocs[uintptr(ptr)]; ok {
		delete(allocs, uintptr(ptr))
	} else {
		panic("free: invalid pointer")
	}
}

// Read memory
func readBufferFromMemory(bufferPosition *uint32, length uint32) []byte {
	subjectBuffer := make([]byte, length)
	pointer := uintptr(unsafe.Pointer(bufferPosition))
	for i := 0; i < int(length); i++ {
		s := *(*int32)(unsafe.Pointer(pointer + uintptr(i)))
		subjectBuffer[i] = byte(s)
	}
	return subjectBuffer
}

// Copy data to memory 
func copyBufferToMemory(buffer []byte) uint64 {
	bufferPtr := &buffer[0]
	unsafePtr := uintptr(unsafe.Pointer(bufferPtr))

	pos := uint32(unsafePtr)
	size := uint32(len(buffer))

	return (uint64(pos) << uint64(32)) | uint64(size)
}

// hello function
//go:wasm-module main
//export hello
func hello(valuePosition *uint32, length uint32) uint64 {
  
  // read the memory to get the argument(s)
  valueBytes := readBufferFromMemory(valuePosition, length)

  message := "👋 Hello " + string(valueBytes) + " 😃"

  // copy the value to memory
  // get the position and the size of the buffer (in memory)
  posSize := copyBufferToMemory([]byte(message))

  // return the position and size
  return posSize

}

func main() {}
