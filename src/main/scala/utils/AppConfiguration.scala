package utils

import java.io.{File, FileInputStream}
import java.net.InetSocketAddress
import java.util

import org.yaml.snakeyaml.Yaml

trait AppConfiguration {
  private var props : java.util.HashMap[String,String] = new util.HashMap

  var proxyHost = ""
  var proxyPort = ""

  def initializeAppConfig(configFilePath : String) : Unit = {

    val fileInputStream = new FileInputStream(new File(configFilePath))
    val confObj = new Yaml().load(fileInputStream)

    props = confObj.asInstanceOf[java.util.HashMap[String,String]]
    proxyHost = props.get("proxyHost")
    proxyPort = props.get("proxyPort")


  }


}
