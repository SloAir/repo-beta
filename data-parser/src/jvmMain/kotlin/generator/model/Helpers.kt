package generator.model

import java.io.File
import java.net.HttpURLConnection
import java.net.URL

fun readFromFile(path: String): List<String> {
    val list: MutableList<String> = mutableListOf()

    File(path).forEachLine { str ->
        list.add(str)
    }

    return list
}


