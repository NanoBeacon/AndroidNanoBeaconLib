package com.oncelabs.nanobeacon.extension

class StringExtensions {
    companion object {
        fun String.decodeHex(): String {
            require(length % 2 == 0) {"Must have an even length"}
            return chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
                .toString(Charsets.UTF_8)
        }
    }
}