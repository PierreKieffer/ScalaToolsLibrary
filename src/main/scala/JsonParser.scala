import scala.io.Source
import scala.util.parsing.json._


object JsonParser {

  /** ---------------------------------------
    * Example of method to parse simple json
        {
        "fields": [
          {
            "field1": "value",
            "field2": "value",
            "field3": "value"
          }
        ]
      }*/

  case class outputData(field1 : String, field2: String, field3 : String)

  def singleMapJsonParser(JsonDataFile : String) : List[outputData] = {

    val JsonData : String = Source.fromFile(JsonDataFile).getLines.mkString

    val jsonFormatData = JSON.parseFull(JsonData).map{
      case json : Map[String, List[Map[String,String]]] =>
        json("fields").map(v => outputData(v("field1"),v("field2"),v("field3")))
    }.get

    jsonFormatData
  }


  /** ---------------------------------------
    * Example of method to parse more complex json (multiple list of elements for example)
           {
            "fields": [
              {
                "field1": "value",
                "field2": [
                  {
                    "field21": "value",
                    "field22": "value"
                  },
                  {
                    "field21": "value",
                    "field22": "value"
                  }
                ]
              }
            ]
          }*/

  case class elementClass(element1 : String, element2 : String)
  case class outputDataClass(field1 : String, exampleClassData : List[elementClass])

  def multipleMapJsonParser(jsonDataFile : String) : List[outputDataClass] = {

    val JsonData : String = Source.fromFile(jsonDataFile).getLines.mkString
    val jsonFormatData = JSON.parseFull(JsonData)
      .map{
        case json : Map[String, List[Map[String,Any]]] => json("fields").map(
          jsonElem =>
            outputDataClass(jsonElem("field1").toString,
              jsonElem("field2").asInstanceOf[List[Map[String,String]]].map{
                case element : Map[String,String] => elementClass(element("field21"),element("field22"))
              })
        )
      }.get

    jsonFormatData

  }
}
