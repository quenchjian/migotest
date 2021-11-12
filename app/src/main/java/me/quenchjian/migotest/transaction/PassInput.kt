package me.quenchjian.migotest.transaction

import me.quenchjian.migotest.network.json.Pass

data class PassInput(val type: Pass.Type, val duration: Int, val rp: Double)