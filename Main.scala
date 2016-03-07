import java.net.URI

import scala.collection.JavaConverters._

import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.openqa.selenium.By
import org.openqa.selenium.htmlunit.HtmlUnitDriver

import argonaut._, Argonaut._

object Main {
  val driver = new HtmlUnitDriver

  def main(args: Array[String]): Unit = {
    accessToken()
  }

  val client_id = ...
  val client_secret = ...
  val password = ...
  val box_login = "user@example.com" // will prefill the username input field
  val redirect_uri = "https://httpbin.org/"
  val state = "required-shit-i-don't-care-about"

  private def accessToken() = {
    val url = new URIBuilder("https://app.box.com/api/oauth2/authorize")
    url.setParameter("response_type", "code")
    url.setParameter("client_id", client_id)
    url.setParameter("redirect_uri", redirect_uri)
    url.setParameter("state", state)
    url.setParameter("box_login", box_login)

    val accessTokenResponse = authorize(url.build.toString)

    println(s"accessTokenResponse: $accessTokenResponse")
  }

  private def authorize(url: String): Option[AccessTokenResponse] = {
    driver.get(url)
    // println(String.format("URL before login: %s", driver.getCurrentUrl()))
    // println(String.format("HTML source before login:\n %s", driver.getPageSource()))

    driver.findElement(By.name("password")).sendKeys(password)
    driver.findElement(By.name("login_submit")).click()
    driver.findElement(By.name("consent_accept")).click()

    URLEncodedUtils.parse(new URI(driver.getCurrentUrl), "UTF-8")
      .asScala
      .find(_.getName == "code")
      .map(_.getValue)
      .flatMap(exchangeCode(_))
  }

  private def exchangeCode(code: String): Option[AccessTokenResponse] = {
    val params = List(
      new BasicNameValuePair("grant_type", "authorization_code"),
      new BasicNameValuePair("code", code),
      new BasicNameValuePair("client_id", client_id),
      new BasicNameValuePair("client_secret", client_secret),
      new BasicNameValuePair("redirect_uri", redirect_uri)
    ).asJava

    val client = HttpClientBuilder.create.build
    val request = new HttpPost("https://app.box.com/api/oauth2/token")
    request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"))

    try {
      val response = client.execute(request)
      val content = EntityUtils.toString(response.getEntity)
      content.decodeOption[AccessTokenResponse]
    } finally {
      client.close()
    }
  }
}

case class AccessTokenResponse(
  accessToken: String,
  expiresIn: Int,
  restrictedTo: List[String],
  refreshToken: String,
  tokenType: String
)

object AccessTokenResponse {
  implicit val decoder: DecodeJson[AccessTokenResponse] =
    jdecode5L(AccessTokenResponse.apply)(
      "access_token",
      "expires_in",
      "restricted_to",
      "refresh_token",
      "token_type"
    )
}
