import zhttp.http.middleware.Cors.CorsConfig

object Cors {
  val config: CorsConfig =
    CorsConfig(
      anyOrigin = false,
      anyMethod = false,
      allowedOrigins = s => s.equals("localhost"),
      allowedMethods = None
    )
}
