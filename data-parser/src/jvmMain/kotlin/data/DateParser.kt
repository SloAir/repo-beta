package data

import java.time.LocalDate
import java.util.*

object DateParser {
    private fun parseDay(day: String): String {
        var str = day

        if(day.length == 1) {
            str = "0$day"
        }

        return str
    }

    private fun parseMonth(month: String): String {
        return when(month) {
            "jan" -> "01"
            "feb" -> "02"
            "mar" -> "03"
            "apr" -> "04"
            "maj" -> "05"
            "jun" -> "06"
            "jul" -> "07"
            "avg" -> "08"
            "sep" -> "09"
            "okt" -> "10"
            "nov" -> "11"
            "dec" -> "12"
            else -> "NA"
        }
    }

    // format example: '13. jun.'
    // function returns date as a string in format YYYY/MM/DD
    fun parseDate(date: String): String {
        val splitDate = date.split('.')
        val dateList = mutableListOf<String>()

        splitDate.forEachIndexed { index, item ->
            dateList.add(item.replace(" ", ""))
        }

        val day = parseDay(dateList[0])
        val month = parseMonth(dateList[0])
        val year = LocalDate.now().year

        return("$year/$month/$day")
    }
}

fun main() {
    println(DateParser.parseDate("1. jun."))
}