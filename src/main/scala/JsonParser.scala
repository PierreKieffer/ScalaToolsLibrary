import scala.io.Source
import scala.util.parsing.json._


object JsonParser {
  
  case class SimpleStructJsonParserData(field1 : String, field2 : String)

  def SimpleStructJsonParser (JsonDataFile : String) : SimpleStructJsonParserData = {

    /** ---------------------------------------
      * Example of method to parse simple json structure 
        {
        "field1": "value",
        "field2": "value",
        "field3": "value"
        }
      }*/
      
    val JsonData : String = Source.fromFile(JsonDataFile).getLines.mkString
    val jsonFormatData = JSON.parseFull(JsonData).map{
      case json : Map[String,Any] =>
        SimpleStructJsonParserData(json("field1").toString,json("field2").toString)
    }.get
    jsonFormatData
  }

  /*
  ___________________________________________________________________________________
 */
  
  case class ListStructJsonParserData(field1 : String, field2: String, field3 : String)

  def ListStructJsonParser(JsonDataFile : String) : List[ListStructJsonParserData] = {

    /** ---------------------------------------
      * Example of method to parse json list structure
        {
        "fields": [
          {
            "field1": "value",
            "field2": "value",
            "field3": "value"
          }
        ]
      }*/

    val JsonData : String = Source.fromFile(JsonDataFile).getLines.mkString
    val jsonFormatData = JSON.parseFull(JsonData).map{
      case json : Map[String, List[Map[String,String]]] =>
        json("fields").map(v => ListStructJsonParserData(v("field1"),v("field2"),v("field3")))
    }.get
    jsonFormatData
  }
  
  /*
  ___________________________________________________________________________________
   */
  
  case class multiStructJsonParser(field1 : String, exampleClassData : List[elementClass])
  case class elementClass(element1 : String, element2 : String)
  
  def multiStructJsonParser(jsonDataFile : String) : List[multiStructJsonParser] = {

    /** ---------------------------------------
      * Example of method to parse json with multiple structures
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

    val JsonData : String = Source.fromFile(jsonDataFile).getLines.mkString
    val jsonFormatData = JSON.parseFull(JsonData)
      .map{
        case json : Map[String, List[Map[String,Any]]] => json("fields").map(
          jsonElem =>
            multiStructJsonParser(jsonElem("field1").toString,
              jsonElem("field2").asInstanceOf[List[Map[String,String]]].map{
                case element : Map[String,String] => elementClass(element("field21"),element("field22"))
              })
        )
      }.get
    jsonFormatData
  }
}
