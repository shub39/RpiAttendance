package shub39.rpi_attendance.client.presentation

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames

fun LocalDate.toFormattedString(): String {
    return this.format(
        LocalDate.Format {
            day()
            chars(" ")
            monthName(MonthNames.ENGLISH_FULL)
            chars(" ")
            year()
        }
    )
}

fun LocalTime.toFormattedString(): String {
    return this.format(
        LocalTime.Format {
            amPmHour()
            chars(":")
            minute()
            chars(" ")
            amPmMarker("AM", "PM")
        }
    )
}