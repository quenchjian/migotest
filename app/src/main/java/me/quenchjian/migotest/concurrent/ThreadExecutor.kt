package me.quenchjian.migotest.concurrent

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreadExecutor @Inject constructor() {

  val io: Executor = ThreadPoolExecutor(3, Int.MAX_VALUE, 60, TimeUnit.SECONDS, LinkedBlockingDeque())
  val ui: Executor = object : Executor {
    private val handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable) {
      handler.post(command)
    }
  }
}