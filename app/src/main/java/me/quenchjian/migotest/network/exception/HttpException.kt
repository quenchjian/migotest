package me.quenchjian.migotest.network.exception

import java.io.IOException

class HttpException(val code: Int, msg: String) : IOException(msg)