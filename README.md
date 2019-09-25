# ScalaToolsLibrary
Library of custom tools to respond to any form of problems, especially data processing.

## JsonParser 
Methods to parse different json structures 
## DataFrameServices
Methods to format different data types to Spark Dataframes
## AkkaServices
Methods to apply Akka http requests
## AkkaWebServer 
Methods to initialize simple Akka web server 
```
object main extends utils.AppConfiguration {
  def main(args: Array[String]): Unit = {
    initializeAppConfig("config.yml")
    AkkaWebServer.initializeWebServer(interface,port.toInt)
  }
}
```

- ``` curl -H "Content-Type: application/json" -X POST -d 'test message' http://localhost:8080/message ```
- ``` curl http://localhost:8080/getData ``` 

## BigTableConnector
Methods to create or Delete tables 
Methods to read from tables and write to tables
## utils 
### AppConfiguration
Configuration file reading tool
### LoggerBase
Log generation tool
### AkkaActor
Akka actor trait
### SparkSessionBase 
SparkSession trait
