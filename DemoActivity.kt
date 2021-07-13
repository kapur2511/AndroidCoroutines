package com.example.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        GlobalScope.launch {
//            launch100000Coroutines()
//        }
//        launch10000Threads()
        GlobalScope.launch {
            func1()
//            makeSomeThirdPartySdkCall()
        }

//        val defaultDispatcher = Dispatchers.Default
//
//        val coroutineErrorHandler = CoroutineExceptionHandler { context, error ->
//            println("Problems with Coroutine: ${error}") // we just print the error here
//        }
//
//        GlobalScope.launch {
//            println(Thread.currentThread().name)
//            throw RuntimeException("Some Error")
//        }
//
//        val emptyParentJob = Job()
//
//        val combinedContext = defaultDispatcher + coroutineErrorHandler + emptyParentJob
//
//        GlobalScope.launch(context = combinedContext) {
//            println(Thread.currentThread().name)
//            throw RuntimeException("Some Error")
//        }
//
//        val parentJob = GlobalScope.launch {
//            func2()
//        }
//
//        val childJob = GlobalScope.launch(parentJob){
//            parentJob.cancel("YOLO")
//            func3()
//        }
//        childJob.cancel("YOLO")

    }

    private suspend fun launch100000Coroutines(){
        val time = measureTimeMillis {
            runBlocking {
                for(i in 1..10000) {
                    launch {
                        delay(10L)
                    }
                }
            }
        }
        println("Took $time ms to complete 10000 iterations each taking using coroutines ~10ms")
    }

    private fun launch10000Threads(){
        val time = measureTimeMillis {
            for(i in 1..10000) {
                Thread(Runnable {
                    Thread.sleep(10)
                }).run()
            }
        }
        println("Took $time ms to complete 10000 iterations using threads each taking ~10ms")
    }

    suspend fun func1(){
        Log.d("SUSPENDED: 1", "STARTING EXECUTION")
        val result = coroutineScope {
            awaitAll(
                async {
                    func2()
                },
                async {
                    func3()
                }
            )
        }

        func4()
        Log.d("SUSPENDED: 1", "RESULT EXECUTION ${result}")
        Log.d("SUSPENDED: 1", "ENDING EXECUTION")
    }

    suspend fun func2(): Int{
        Log.d("SUSPENDED: 2", "STARTING EXECUTION")
        delay(1000)
        Log.d("SUSPENDED: 2", "ENDING EXECUTION")
        return 2
    }

    suspend fun func3(): String{
        Log.d("SUSPENDED: 3", "STARTING EXECUTION")
        delay(4000)
        throw java.lang.RuntimeException("lol")
        Log.d("SUSPENDED: 3", "ENDING EXECUTION")
        return "func3"
    }

    suspend fun func4(): Int{
        Log.d("SUSPENDED: 4", "STARTING EXECUTION")
        delay(3000)
        Log.d("SUSPENDED: 4", "ENDING EXECUTION")
        return 4
    }

    suspend fun funcA(): Int{
        Log.d("SUSPENDED: A", "STARTING EXECUTION")
        //suspension point or label
        funcB()
        //suspension point or label
        funcC()
        Log.d("SUSPENDED: A", "ENDING EXECUTION")
        return 4
    }

    suspend fun funcB(): Int{
        Log.d("SUSPENDED: B", "STARTING EXECUTION")
        delay(3000)
        Log.d("SUSPENDED: B", "ENDING EXECUTION")
        return 4
    }

    suspend fun funcC(): Int{
        Log.d("SUSPENDED: C", "STARTING EXECUTION")
        delay(3000)
        Log.d("SUSPENDED: C", "ENDING EXECUTION")
        return 4
    }






    @ExperimentalCoroutinesApi
    suspend fun makeSomeThirdPartySdkCall() {
        Log.d("makeThirdPartySdkCall", "STARTING EXECUTION")
        var result: String? = "lol"
        result = consumeResponseSuspendedWrapper { apiCompletionCallback: APICompletionCallback ->
            sdkFunction(apiCompletionCallback)
        }
        Log.d("makeThirdPartySdkCall", "ENDING EXECUTION $result")
    }

    fun sdkFunction(apiCompletionCallback: APICompletionCallback){
        Log.d("sdkFunction", "STARTING EXECUTION")
        Thread.sleep(4000)
        Log.d("sdkFunction", "ENDING EXECUTION")
        apiCompletionCallback.onResult("bla bla bla")
    }

    @ExperimentalCoroutinesApi
    private suspend fun consumeResponseSuspendedWrapper(
        block: (APICompletionCallback) -> Unit
    ): String? {
        Log.d("consumeResponseSuspen", "STARTING EXECUTION")
        return suspendCancellableCoroutine{ cont: CancellableContinuation<String?> ->
            Log.d("consumeResponseSuspen", "suspendCancellableCoroutine EXECUTION")
            block(
                object : APICompletionCallback {
                    override fun onResult(response: String) {
                        Log.d("consumeResponseSuspen", "onResult")
                        if (response != null) {
                            cont.resume(response, null)
                        } else {
                            cont.resume(null, null)
                        }
                    }
                }
            )
        }
    }
}

interface APICompletionCallback {
    fun onResult(response: String)
}
