package com.qhy040404.mihoyogacha.utils

import java.security.MessageDigest

fun md5(byteArray: ByteArray): String = digest(byteArray, "MD5")

fun sha1(byteArray: ByteArray): String = digest(byteArray, "SHA-1")

fun sha256(byteArray: ByteArray): String = digest(byteArray, "SHA-256")

fun sha512(byteArray: ByteArray): String = digest(byteArray, "SHA-512")

private fun digest(byteArray: ByteArray, algorithm: String): String {
    val messageDigest = MessageDigest.getInstance(algorithm).apply {
        reset()
    }
    messageDigest.update(byteArray)
    return messageDigest.digest().toHex()
}

private fun ByteArray.toHex(): String {
    return buildString {
        this@toHex.forEach {
            var hex = Integer.toHexString(it.toInt() and 0xFF)
            if (hex.length == 1) {
                hex = "0$hex"
            }
            append(hex.lowercase())
        }
    }
}