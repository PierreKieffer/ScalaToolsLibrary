package utils

import org.apache.log4j.Logger

trait LoggerBase {

  val logger = Logger.getLogger(this.getClass.getName)


  def writeLog(level : String, message : String) : Unit = level match {

    case "info" => logger.info(
      s"INFO : $message")

    case "warn" => logger.warn(
      s" WARN : $message")
    case "error" => logger.error(
      s"""
         |
        |    ERROR : $message
         |
      """.stripMargin)
  }


}
