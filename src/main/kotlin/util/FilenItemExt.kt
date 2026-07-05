package com.example.util

import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import java.io.File
import java.util.*

suspend fun PartData.FileItem.save(path: String): String {

    val fileByte = provider().readRemaining().readByteArray()
    val fileExtension = originalFileName?.substringAfterLast('.', "")
    val fileName = UUID.randomUUID().toString() + "." + fileExtension

    val folder = File(path)
    folder.mkdirs()

    File("$path$fileName").writeBytes(fileByte)

    return fileName
}