package com.beeswork.balance.internal.constant

import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class DateTimePattern {

    companion object {

        private const val DATE_WITH_DAY_OF_WEEK_IN_KOREA = "yyyy년 M월 dd일 EEEE"
        private const val DATE_WITH_DAY_OF_WEEK_IN_ENGLISH = "EEEE, dd MMM yyyy"

        private const val TIME_WITH_MERIDIEM_IN_ENGLISH = "h:mm a"
        private const val TIME_WITH_MERIDIEM_IN_KOREA = "a h:mm"

        private const val DATE_WITHOUT_YEAR = "yy/M/d"

        fun ofDateWithDayOfWeek(): DateTimeFormatter {
            return when (Locale.getDefault()) {
                Locale.KOREA -> DateTimeFormatter.ofPattern(DATE_WITH_DAY_OF_WEEK_IN_KOREA)
                Locale.KOREAN -> DateTimeFormatter.ofPattern(DATE_WITH_DAY_OF_WEEK_IN_KOREA)
                else -> DateTimeFormatter.ofPattern(DATE_WITH_DAY_OF_WEEK_IN_ENGLISH)
            }
        }

        fun ofTimeWithMeridiem(): DateTimeFormatter {
            return when (Locale.getDefault()) {
                Locale.KOREA -> DateTimeFormatter.ofPattern(TIME_WITH_MERIDIEM_IN_KOREA)
                Locale.KOREAN -> DateTimeFormatter.ofPattern(TIME_WITH_MERIDIEM_IN_KOREA)
                else -> DateTimeFormatter.ofPattern(TIME_WITH_MERIDIEM_IN_ENGLISH)
            }
        }

        fun ofDateWithShortYear(): DateTimeFormatter {
            return DateTimeFormatter.ofPattern(DATE_WITHOUT_YEAR)
        }
    }
}