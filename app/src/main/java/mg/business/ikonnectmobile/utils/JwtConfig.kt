package mg.business.ikonnectmobile.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*


data class TokenWithExpiration(val token: String, val expiration: Date)

object JwtConfig {
    private const val secret = "1k0m!2024.f/tsoa"
    private const val issuer = "ktor.io"
    private val algorithm = Algorithm.HMAC512(secret)
    private const val accessTokenValidityMs = 720000 //12 min
    private const val refreshTokenValidityMs = 86400000 // 24 h


    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun makeAccessToken(username: String): String = JWT.create()
        .withClaim("name", username)
        .withClaim("type", "access")
        .withIssuer(issuer)
        .withExpiresAt(getExpiration(accessTokenValidityMs))
        .sign(algorithm)

    fun makeRefreshToken(userId: Int): TokenWithExpiration {
        val expirationDate = getExpiration(refreshTokenValidityMs)
        val token = JWT.create()
            .withClaim("user_id", userId)
            .withClaim("type", "refresh")
            .withIssuer(issuer)
            .withExpiresAt(expirationDate)
            .sign(algorithm)

        return TokenWithExpiration(token, expirationDate)
    }

    fun isRefreshToken(token: String): Boolean {
        val decodedJWT = verifier.verify(token)
        return decodedJWT.getClaim("type").asString() == "refresh"
    }

    fun getExpiration(validityInMs: Int) = Date(System.currentTimeMillis() + validityInMs)
}
