package auth

import javax.inject.Inject

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import play.api.Configuration

class JWTUtil @Inject()()(config: Configuration) {
  val JwtSecretKey: String = config.get[String]("jwt.secretkey")
  val JwtSecretAlgo: String = config.get[String]("jwt.secretalgo")

  def createToken(payload: String): String = {
    val header = JwtHeader(JwtSecretAlgo)
    val claimsSet = JwtClaimsSet(payload)

    JsonWebToken(header, claimsSet, JwtSecretKey)
  }

  def isValidToken(jwtToken: String): Boolean =
    JsonWebToken.validate(jwtToken, JwtSecretKey)

  def decodePayload(jwtToken: String): Option[String] =
    jwtToken match {
      case JsonWebToken(_, claimsSet, _) => Option(claimsSet.asJsonString)
      case _ => None
    }
}
