package za.ac.up.emk310

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.html.respondHtml
import io.ktor.server.html.respondHtmlFragment
import io.ktor.server.http.content.staticResources
import kotlinx.html.*
import kotlin.random.Random
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import io.ktor.server.http.content.*

fun Application.configureRouting() {
    routing {
        val random = Random(System.currentTimeMillis())
        
        staticResources("/", "/web")
        
        get("/") {
            call.respondHtml {
                leaderboardPage(random)
            }
        }
        
        get("/more-rows") {
            call.respondHtmlFragment {
                table {
                    tbody {
                        randomRows(random)
                    }
                }
            }
        }
        authenticate("auth-oauth-google") {
            get("login") {
                call.respondRedirect("/callback")
            }
        
            get("/callback") {
                val principal: OAuthAccessTokenResponse.OAuth2? = call.authentication.principal()
                call.sessions.set(UserSession(principal?.accessToken.toString()))
                call.respondRedirect("/hello")
            }
        }
        staticResources("/static", "static")
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}