package me.quenchjian.migotest.network.json

import java.time.LocalDateTime

data class Pass(
  val id: Int = 0,
  val start: LocalDateTime = LocalDateTime.now(),
  val end: LocalDateTime = LocalDateTime.now(),
  val type: Type = Type.DAY,
  val duration: Int = 0,
  val rp: Double = 0.0,
  val createDate: LocalDateTime = LocalDateTime.now(),
  val subscribed: Boolean = false,
) {

  enum class Type {
    DAY, HOUR
  }
}