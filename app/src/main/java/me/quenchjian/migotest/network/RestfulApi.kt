package me.quenchjian.migotest.network

import androidx.annotation.WorkerThread
import me.quenchjian.migotest.network.json.Pass

@WorkerThread
interface RestfulApi {

  var usePrivate: Boolean

  fun status(): String
  fun loadPass(): List<Pass>
  fun addPass(type: Pass.Type, duration: Int, rp: Double): Pass
  fun subscribe(passId: Int): Pass
  fun loadSubscriptions(): List<Pass>
}