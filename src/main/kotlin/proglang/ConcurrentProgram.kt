package proglang

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ConcurrentProgram(val threadBodies: List<Stmt>, val pauseValues: List<Long>){
    init{
        if (threadBodies.size != pauseValues.size) {
            throw IllegalArgumentException("sizes not equal")
        }
    }
    private val lock: Lock = ReentrantLock()
    fun execute(initialStore: Map<String,Int>): MutableMap<String,Int>{
        val workingStore = mutableMapOf<String,Int>()
        initialStore.entries.forEach {(k,v) -> workingStore.put(k,v)}
        val n = threadBodies.size
        val threads = (0..<n).map {i -> Thread(ProgramExecutor(threadBodies[i],pauseValues[i], workingStore, lock ))}
        try {
            threads.forEach{it.start()}
        }
        catch(_: UndefinedBehaviourException) {
            throw UndefinedBehaviourException("")
        } finally {
            threads.forEach{it.join()}
        }
        return workingStore



    }



}
