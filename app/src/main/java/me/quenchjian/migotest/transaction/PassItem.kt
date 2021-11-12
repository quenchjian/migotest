package me.quenchjian.migotest.transaction

import android.os.Parcel
import android.os.Parcelable
import me.quenchjian.migotest.network.json.Pass
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class PassItem(
  val isHeader: Boolean = false,
  val type: Pass.Type = Pass.Type.DAY,
  val pass: Pass? = null,
) : Parcelable {

  constructor(parcel: Parcel) : this(
    parcel.readByte() != 0.toByte(),
    Pass.Type.valueOf(parcel.readString()!!),
    if (parcel.readByte() == 0.toByte()) null else Pass(
      id = parcel.readInt(),
      start = parcel.readLong().toLocalDateTime(),
      end = parcel.readLong().toLocalDateTime(),
      type = Pass.Type.valueOf(parcel.readString()!!),
      duration = parcel.readInt(),
      rp = parcel.readDouble(),
      createDate = parcel.readLong().toLocalDateTime(),
      subscribed = parcel.readByte() != 0.toByte()
    ))

  fun isExpired(): Boolean = pass != null && pass.subscribed && LocalDateTime.now().isAfter(pass.end)

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeByte(if (isHeader) 1 else 0)
    parcel.writeString(type.name)
    if (pass == null) {
      parcel.writeByte(0)
    } else {
      parcel.writeByte(1)
      parcel.writeInt(pass.id)
      parcel.writeLong(pass.start.toLong())
      parcel.writeLong(pass.end.toLong())
      parcel.writeString(pass.type.name)
      parcel.writeInt(pass.duration)
      parcel.writeDouble(pass.rp)
      parcel.writeLong(pass.createDate.toLong())
      parcel.writeByte(if (pass.subscribed) 1 else 0)
    }
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<PassItem> {

    private fun LocalDateTime.toLong(): Long {
      return atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun Long.toLocalDateTime(): LocalDateTime {
      return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    }

    override fun createFromParcel(parcel: Parcel): PassItem {
      return PassItem(parcel)
    }

    override fun newArray(size: Int): Array<PassItem?> {
      return arrayOfNulls(size)
    }
  }
}