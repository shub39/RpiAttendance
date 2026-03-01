/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
import data.AdminInterfaceImpl
import data.SensorServerImpl
import data.database.getRoomDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.curl.Curl
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.serialization.json.Json

fun main() {
    // initializing stuff
    val client =
        HttpClient(Curl) {
            install(ContentNegotiation) { json(json = Json { ignoreUnknownKeys = true }) }

            engine {
                sslVerify = true
                caInfo = "/etc/ssl/certs/ca-certificates.crt"
                caPath = "/etc/ssl/certs"
            }
        }
    val db = getRoomDatabase()
    val studentDao = db.studentDao()
    val teachDao = db.teacherDao()
    val attendanceLogDao = db.attendanceLogDao()

    val sensorServer = SensorServerImpl(client = client)
    val adminInterface =
        AdminInterfaceImpl(
            studentDao = studentDao,
            teacherDao = teachDao,
            attendanceLogDao = attendanceLogDao,
            sensorServer = sensorServer,
        )

    // admin server to be accessed on client apps
    val server =
        embeddedServer(CIO, host = "0.0.0.0", port = 8080) {
            install(Krpc)
            routing {
                rpc("/rpc") {
                    rpcConfig { serialization { json { allowStructuredMapKeys = true } } }

                    registerService<AdminInterface> { adminInterface }
                }
            }
        }

    server.start(wait = false)

    runBlocking {
        mainLoop(
            sensorServer = sensorServer,
            attendanceLogDao = attendanceLogDao,
            studentDao = studentDao,
            teacherDao = teachDao,
        )
    }

    client.close()
    server.stop(1000, 5000)
    logInfo("Server Stopped")
}
