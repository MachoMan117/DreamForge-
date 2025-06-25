
package com.dreamforge.collab

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.socket.*
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.*

@SpringBootApplication
class CollabServer

fun main(args: Array<String>) {
    runApplication<CollabServer>(*args)
}

@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(ProjectCollabHandler(), "/collab").setAllowedOrigins("*")
    }
}

class ProjectCollabHandler : TextWebSocketHandler() {
    private val sessions = mutableListOf<WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
        println("🟢 New collaborator joined: ${session.id}")
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        println("✉️ Message from ${session.id}: ${message.payload}")
        sessions.forEach {
            if (it != session) it.sendMessage(TextMessage("🧠 Update from ${session.id}: ${message.payload}"))
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
        println("🔴 Collaborator left: ${session.id}")
    }
}
