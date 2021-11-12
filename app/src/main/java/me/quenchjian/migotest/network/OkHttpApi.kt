package me.quenchjian.migotest.network

import me.quenchjian.migotest.network.exception.HttpException
import me.quenchjian.migotest.network.json.Pass
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OkHttpApi @Inject constructor(
  private val client: OkHttpClient,
  private val monitor: NetworkMonitor,
) : RestfulApi {

  override var usePrivate: Boolean = monitor.checkPrivate()

  private val publicUrl: String = "https://code-test.migoinc-dev.com/status"
  private val privateUrl: String = "http://192.168.2.2/status"
  private val passes = mutableListOf(
    Pass(1, type = Pass.Type.DAY, duration = 1, rp = 2.0),
    Pass(2, type = Pass.Type.DAY, duration = 3, rp = 5.0),
    Pass(3, type = Pass.Type.DAY, duration = 7, rp = 10.0),
    Pass(4, type = Pass.Type.HOUR, duration = 1, rp = 500.0),
    Pass(5, type = Pass.Type.HOUR, duration = 8, rp = 1.0),
  )
  private val subscriptions = mutableListOf<Pass>()

  init {
    monitor.addCapabilitiesChangeListener { usePrivate = monitor.checkPrivate() }
  }

  override fun status(): String {
    val url = if (usePrivate) privateUrl else publicUrl
    val request = Request.Builder().url(url).build()
    val response = client.newCall(request).execute()
    if (!response.isSuccessful) throw HttpException(response.code, response.message)
    val body = response.body ?: throw HttpException(response.code, "Response Body is null")
    return body.string()
  }

  override fun loadPass(): List<Pass> {
    return passes.toList()
  }

  override fun addPass(type: Pass.Type, duration: Int, rp: Double): Pass {
    val pass = Pass(id = passes.size + 1, type = type, duration = duration, rp = rp)
    passes.add(pass)
    return pass
  }

  override fun subscribe(passId: Int): Pass {
    val pass = passes.find { it.id == passId } ?: throw HttpException(600, "Pass doesn't exist")
    val subscribed = subscriptions.find { it.id == passId }
    if (subscribed != null) throw HttpException(601, "Pass already subscribed")
    val start = LocalDateTime.now()
    val end = when (pass.type) {
      Pass.Type.DAY -> start.truncatedTo(ChronoUnit.DAYS).plusDays(pass.duration.toLong() + 1)
      Pass.Type.HOUR -> start.plusHours(pass.duration.toLong())
    }
    return pass.copy(start = start, end = end, subscribed = true).also { subscriptions.add(it) }
  }

  override fun loadSubscriptions(): List<Pass> {
    return subscriptions.toList()
  }
}