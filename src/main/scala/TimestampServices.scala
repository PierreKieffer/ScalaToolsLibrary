import java.time.{Instant, LocalDateTime, LocalTime, Period}

object TimestampServices {

  case class timestamp(yesterdayMidnight : Long,todayMidnight : Long, tomorrowMidnight : Long)

  def timeDefinition() : timestamp = {
    /** Method to define time values*/
    val dateOfDay = java.time.LocalDate.now
    val yesterday = dateOfDay.minus(Period.ofDays(1))
    val tomorrow = dateOfDay.plus(Period.ofDays(1))

    val midnight = LocalTime.MIDNIGHT
    val todayMidnight : Long = Instant.parse(LocalDateTime.of(dateOfDay, midnight).toString + ":00Z").getEpochSecond*1000
    val yesterdayMidnight = Instant.parse(LocalDateTime.of(yesterday, midnight).toString + ":00Z").getEpochSecond*1000
    val tomorrowMidnight = Instant.parse(LocalDateTime.of(tomorrow, midnight).toString + ":00Z").getEpochSecond*1000

    timestamp(yesterdayMidnight,todayMidnight, tomorrowMidnight)

  }

}
