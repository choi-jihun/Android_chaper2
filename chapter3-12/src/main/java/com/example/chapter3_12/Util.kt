package com.example.chapter3_12

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException

fun <T>Context.readData(fileName: String, classT: Class<T>):T? {
    return try{
        val inputStream = this.resources.assets.open(fileName)
        val byteArray = ByteArray(inputStream.available())
        inputStream.read(byteArray)
        inputStream.close()

        val gson = Gson()
        gson.fromJson(String(byteArray), classT)
    } catch (e:IOException) {
        null
    } catch (e:JsonSyntaxException) {
        null
    }
}