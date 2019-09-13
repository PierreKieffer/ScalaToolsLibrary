import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object DataFrameServices {


  def regExColNames (inputDataframe : DataFrame) : DataFrame = {

    /** Format column names according to regex rule*/

    var outputDataFrame = inputDataframe

    for(col <- inputDataframe.columns){
      outputDataFrame = outputDataFrame.withColumnRenamed(col, col.replaceAll("[\\s\\(\\),\\;\\=]", "_"))
    }

    outputDataFrame
  }


  def stringToDF(sparkSession: SparkSession, inputString : String, header : Boolean, lineSeparator : String, fieldSeparator : String) : DataFrame = {

    /** Format string to spark dataframe
      * val input = """Row01,Value1,value2,value3,value4
      * Row1,Value11,value12,value13,value14
      * Row2,Value21,value22,value23,value24
      * Row3,Value31,value32,value33,value34"""
      *
      * DataFrameServices.stringToDF(sparkSession,input,true,"\n",",")
      *
      * +-----+-------+-------+-------+-------+
      * |Row01| Value1| value2| value3| value4|
      * +-----+-------+-------+-------+-------+
      * ||Row1|Value11|value12|value13|value14|
      * ||Row2|Value21|value22|value23|value24|
      * ||Row3|Value31|value32|value33|value34|
      * +-----+-------+-------+-------+-------+*/

    import sparkSession.implicits._

    val dataset = inputString.split(lineSeparator).map(_.trim)

    if (header) {

      val columns = dataset(0).split(fieldSeparator)
      val numbOfCol = columns.length

      val outputDataFrame = dataset.drop(1).toSeq.toDF("columns")
        .withColumn("temp",split(col("columns"),fieldSeparator))
        .select((0 to numbOfCol-1).map( i => col("temp").getItem(i).as(columns(i))):_*)
      outputDataFrame

    } else {

      val numbOfCol = dataset(0).split(fieldSeparator).length

      val outputDataFrame = dataset.toSeq.toDF("columns")
        .withColumn("temp",split(col("columns"),fieldSeparator))
        .select((0 to numbOfCol-1).map( i => col("temp").getItem(i).as("col_"+i)):_*)

      outputDataFrame
    }
  }


  def jsonToDF(sparkSession: SparkSession, inputJson : String) : DataFrame = {

    /** Format json string to spark dataframe
      * val input = "{"id":"id_example","list":[{"timestamp":"1568375546","Field1":{"value1":-24,"value2":12.10},"Field2":{"value1":"example_val","value2":"example_val"}},{"timestamp":"1568375546","Field1":{"value1":-24,"value2":12.10},"Field2":{"value1":"example_val","value2":"example_val"}}]}"
      *
      *
      *
      * val df = DataFrameServices.jsonToDF(sparkSession, input)
      *
      *
      * +----------+-------------+-------------+-------------+-------------+
      * |        id|Field1_value1|Field1_value2|Field2_value1|Field2_value2|
      * +----------+-------------+-------------+-------------+-------------+
      * |id_example|          -24|         12.1|  example_val|  example_val|
      * |id_example|          -24|         12.1|  example_val|  example_val|
      * +----------+-------------+-------------+-------------+-------------+*/

    import sparkSession.implicits._

    val outputDataFrame = sparkSession.read.option("multiline", true).option("mode","PERMISSIVE")
      .json(Seq(inputJson).toDS)
        .select("id","list")
      .withColumn("list", explode(col("list")))
      .select( /* First layer mapping */
        col("id"),
        col("list").getItem("Field1").as("Field1"),
        col("list").getItem("Field2").as("Field2")
      )
        .select( /* Second layer mapping */
        col("id"),
          col("Field1").getItem("value1").as("Field1_value1"),
          col("Field1").getItem("value2").as("Field1_value2"),
          col("Field2").getItem("value1").as("Field2_value1"),
          col("Field2").getItem("value2").as("Field2_value2")
        )

    outputDataFrame
  }
}
