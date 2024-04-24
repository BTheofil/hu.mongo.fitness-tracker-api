package hu.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import hu.mongo.application.routes.fitnessRoutes
import hu.mongo.domain.ports.FitnessRepository
import hu.mongo.infrastructure.repository.FitnessRepositoryImpl
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        gson {
        }
    }
    install(Koin) {
        slf4jLogger()
        modules(module {
            single {
                MongoClient.create(
                    environment.config.propertyOrNull("ktor.mongo.uri")?.getString()
                        ?: throw RuntimeException("Failed to access MongoDB URI.")
                )
            }
            single { get<MongoClient>().getDatabase(environment.config.property("ktor.mongo.database").getString()) }
        }, module {
            single<FitnessRepository> { FitnessRepositoryImpl(get()) }
        })
    }
    routing {
        swaggerUI(path = "swagger-ui", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5"
        }
        fitnessRoutes()
    }
}
