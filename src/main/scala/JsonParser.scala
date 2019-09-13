import scala.io.Source
import scala.util.parsing.json._


object JsonParser {

  case class data(field1 : String, field2: String, field3 : String)

  /** Example of method to parse simple json
    * {
    * "fields": [
    * {
    * "field1": "value1",
    * "field2": "value2",
    * "field3": "value3"
    * }
    * ]
    * }
    * */

  def fromJsonToClass (JsonDataFile : String) : List[data] = {
    val JsonData : String = Source.fromFile(JsonDataFile).getLines.mkString

    val jsonFormatData = JSON.parseFull(JsonData).map{
      case json : Map[String, List[Map[String,String]]] =>
        json("fields").map(v => data(v("field1"),v("field2"),v("field3")))
    }.get

    jsonFormatData
  }

}
