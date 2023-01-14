package my.kopring.setting.utils

import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.PeriodFormatterBuilder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

object DateTimeUtils {
    private val RAW_OFFSET = TimeZone.getDefault().rawOffset.toLong()

    fun toMillis(localDateTime: LocalDateTime): Long {
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun currentMills(): Long {
        return toMillis(LocalDateTime.now())
    }

    fun toLocalDateTime(time: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
    }

    fun toDuration(millis: Long): String {
        val duration = Duration.millis(millis)
        val period = duration.toPeriod()
        val pf = PeriodFormatterBuilder() //
            .printZeroAlways() //
            .minimumPrintedDigits(2).appendHours() //
            .appendSeparator(":") //
            .appendMinutes() //
            .appendSeparator(":") //
            .appendSeconds() //
            .toFormatter()
        return pf.print(period)
    }

    private fun toMinutes(gmt: Double): Int {
        return (gmt * 60).toInt()
    }

    fun addGmt(dt: LocalDateTime, gmt: Double): LocalDateTime {
        return if (RAW_OFFSET == 0L) // 서버의 타임존은 UTC 이므로 RAW_OFFSET 값은 0.
            dt.plusMinutes(toMinutes(gmt).toLong()) else  // 로컬에서 실행 시에는 로컬의 시스템 타임존으로 처리할 수 있게.
            dt
    }

    fun yyyyMMddToFormatDateStr(yyyymmdd: String): String {
        return yyyymmdd.substring(0, 4) + "-" + yyyymmdd.substring(4, 6) + "-" + yyyymmdd.substring(6)
    }

    fun toFormatString(localDateTime: LocalDateTime): String {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    fun toFormatString(num: Long): String {
        val localDateTime = this.toLocalDateTime(num)
        return toFormatString(localDateTime)
    }

    fun toFormatString(num: Long, gmt: Double): String {
        val localDateTime = this.toLocalDateTime(num)
        val added = this.addGmt(localDateTime, gmt)
        return toFormatString(added)
    }

    fun toFormatString(localDateTime: LocalDateTime, gmt: Double): String {
        val added = this.addGmt(localDateTime, gmt)
        return toFormatString(added)
    }
}