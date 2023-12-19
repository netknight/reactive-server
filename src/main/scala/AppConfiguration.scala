package io.dm

import pureconfig.ConfigReader
import pureconfig.generic._
//import pureconfig.generic.derivation.default._

final case class AppConfiguration(http: HttpConfiguration) //derives ConfigReader
final case class HttpConfiguration(host: String, port: Int) //derives ConfigReader

object AppConfiguration {

  //given httpConfigurationReader: ConfigReader[HttpConfiguration] = deriveReader[HttpConfiguration]


}
