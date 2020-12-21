package com.babestudios.ganbb.testhelpers

import java.io.IOException
import java.io.InputStream


class JsonHelper {
	fun loadJson(jsonFileName: String): String {
		val inputStream = this.javaClass.classLoader?.getResourceAsStream("$jsonFileName.json")
		inputStream?.let {
			return readString(inputStream)
		} ?: run { return "" }
	}

	@Throws(IOException::class)
	fun readString(stream: InputStream): String {
		return String(stream.readBytes(), Charsets.UTF_8)
	}
}
