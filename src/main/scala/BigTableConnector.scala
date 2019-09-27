import org.apache.hadoop.hbase.client.{Get, Put}

object BigTableConnector {

  import org.apache.hadoop.hbase.util.Bytes
  import com.google.cloud.bigtable.hbase.BigtableConfiguration
  import org.apache.hadoop.hbase.HColumnDescriptor
  import org.apache.hadoop.hbase.HTableDescriptor
  import org.apache.hadoop.hbase.client.Scan

  import org.apache.hadoop.hbase.TableName


  import java.io.IOException

  // java -jar  -Dbigtable.projectID=your_project_ID  -Dbigtable.instanceID=your_big_table_name BigTableConnector-assembly-0.1.jar

  private val TABLE_NAME = Bytes.toBytes("table_test")
  private val COLUMN_FAMILY_NAME = Bytes.toBytes("family_test")
  private val COLUMN_NAME = Bytes.toBytes("message")


  def writeToBigTable(projectId: String, instanceId: String): Unit = {

    try {
      val connection = BigtableConfiguration.connect(projectId, instanceId)

      try {
        val table = connection.getTable(TableName.valueOf(TABLE_NAME))

        var k = 0
        val message = "hello world"
        while (k<300){
          val rowKey = "key_example" + k
                    val put = new Put(Bytes.toBytes(rowKey))
                    put.addColumn(COLUMN_FAMILY_NAME, COLUMN_NAME, Bytes.toBytes(message))
                    table.put(put)
            k +=1
        }
      } catch {
        case e: IOException =>
          System.err.println("Exception: " + e.getMessage)
          e.printStackTrace()
          System.exit(1)
      } finally if (connection != null) connection.close()
    } catch {
      case e: IOException =>
        System.err.println("Exception: " + e.getMessage)
        e.printStackTrace()
        System.exit(1)
    }

    System.exit(0)
  }


  def readFromBigTable(projectId: String, instanceId: String): Unit = {

    try {
      val connection = BigtableConfiguration.connect(projectId, instanceId)

      try {
        val table = connection.getTable(TableName.valueOf(TABLE_NAME))

        // Get a row by key
        val rowKey = "key_example"
        val getResult = table.get(new Get(Bytes.toBytes(rowKey)))
        val data = Bytes.toString(getResult.getValue(COLUMN_FAMILY_NAME, COLUMN_NAME))
        System.out.println("Get data by row key")
        System.out.printf("\t%s = %s\n", rowKey, data)

        // Scan all rows.
        val scan = new Scan
        val scanner = table.getScanner(scan)
        import scala.collection.JavaConversions._
        for (row <- scanner) {
          val valueBytes = row.getValue(COLUMN_FAMILY_NAME, COLUMN_NAME)
          System.out.println('\t' + Bytes.toString(valueBytes))
        }

      }catch {
            case e: IOException =>
              System.err.println("Exception: " + e.getMessage)
              e.printStackTrace()
              System.exit(1)
          } finally if (connection != null) connection.close()
        } catch {
          case e: IOException =>
            System.err.println("Exception: " + e.getMessage)
            e.printStackTrace()
            System.exit(1)
        }

        System.exit(0)
  }


  def createTable(projectId: String, instanceId: String) : Unit = {
    try {
      val connection = BigtableConfiguration.connect(projectId, instanceId)

      try {
        val admin = connection.getAdmin

        val descriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME))
        descriptor.addFamily(new HColumnDescriptor(COLUMN_FAMILY_NAME))
        println("Create table " + descriptor.getNameAsString)

        admin.createTable(descriptor)

      }catch {
        case e: IOException =>
          System.err.println("Exception: " + e.getMessage)
          e.printStackTrace()
          System.exit(1)
      } finally if (connection != null) connection.close()
    } catch {
      case e: IOException =>
        System.err.println("Exception: " + e.getMessage)
        e.printStackTrace()
        System.exit(1)
    }

    System.exit(0)

  }


  def deleteTable(projectId: String, instanceId: String) : Unit = {
    try {
      val connection = BigtableConfiguration.connect(projectId, instanceId)

      try {
        val admin = connection.getAdmin
        val table = connection.getTable(TableName.valueOf(TABLE_NAME))

        println("Delete the table")
        admin.disableTable(table.getName)
        admin.deleteTable(table.getName)


      }catch {
        case e: IOException =>
          System.err.println("Exception: " + e.getMessage)
          e.printStackTrace()
          System.exit(1)
      } finally if (connection != null) connection.close()
    } catch {
      case e: IOException =>
        System.err.println("Exception: " + e.getMessage)
        e.printStackTrace()
        System.exit(1)
    }

    System.exit(0)

  }

  def main(args: Array[String]): Unit = {
    val projectId = requiredProperty("bigtable.projectID")
    val instanceId = requiredProperty("bigtable.instanceID")
    createTable(projectId, instanceId)
    writeToBigTable(projectId, instanceId)
    readFromBigTable(projectId,instanceId)
    deleteTable(projectId,instanceId)
  }

  private def requiredProperty(prop: String) = {
    val value = System.getProperty(prop)
    if (value == null) throw new IllegalArgumentException("Missing required system property: " + prop)
    value
  }

}
