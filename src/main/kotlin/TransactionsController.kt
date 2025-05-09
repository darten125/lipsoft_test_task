package com.example

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.io.File

class TransactionsController(private val call: ApplicationCall) {
    suspend fun updateTransactionsData(){
        val tmpDir = File("uploads").apply { mkdirs() }
        try {
            Database.init()
            val multipart = call.receiveMultipart()
            var incomesPath: String? = null
            var outcomesPath: String? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val file = File(tmpDir, part.originalFileName!!)
                    part.streamProvider().use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    if (file.name.contains("income", true))  incomesPath = file.absolutePath
                    if (file.name.contains("outcome", true)) outcomesPath = file.absolutePath
                }
                part.dispose()
            }

            if (incomesPath==null || outcomesPath==null){
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Both incomes and outcomes files are required"
                )
                return
            }

            Database.importCsv(incomesPath!!)
            Database.importCsv(outcomesPath!!)
            tmpDir.listFiles()?.forEach { it.delete() }

            call.respond(
                HttpStatusCode.OK,
                "Transactions updated"
            )

        } catch (t:Throwable){
            call.respond(
                HttpStatusCode.InternalServerError,
                "Error: ${t.message ?: "Unknown error"}"
            )
        }
    }

    suspend fun getUserTransactions() {
        try {
            val customerId = call.parameters["customerId"]
                ?: return call.respond(HttpStatusCode.BadRequest, "Missing or invalid customerId")
            val cursor = call.parameters["cursor"]?.takeIf { it.isNotBlank() }
            val response = Database.fetchUserTransactions(customerId, cursor)
            call.respond(HttpStatusCode.OK, response)
        } catch (t: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "Error: ${t.message ?: "Unknown error"}"
            )
        }
    }

    suspend fun getUserAccountTransactions() {
        try {
            val customerId = call.parameters["customerId"]
                ?: return call.respond(HttpStatusCode.BadRequest, "Missing or invalid customerId")
            val accountId = call.parameters["accountId"]
                ?: return call.respond(HttpStatusCode.BadRequest, "Missing or invalid accountId")
            val cursor = call.parameters["cursor"]?.takeIf { it.isNotBlank() }
            val response = Database.fetchUserAccountTransactions(customerId, accountId, cursor)
            call.respond(HttpStatusCode.OK, response)
        } catch (t: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "Error: ${t.message ?: "Unknown error"}"
            )
        }
    }

    suspend fun getUserTransactionsByDate() {
        try {
            val customerId = call.parameters["customerId"]
                ?: return call.respond(HttpStatusCode.BadRequest, "Missing or invalid customerId")
            val date = call.parameters["date"]
                ?: return call.respond(HttpStatusCode.BadRequest, "Missing date")
            val cursor = call.parameters["cursor"]?.takeIf { it.isNotBlank() }
            val response = Database.fetchUserTransactionsByDate(customerId, date, cursor)
            call.respond(HttpStatusCode.OK, response)
        } catch (t: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "Error: ${t.message ?: "Unknown error"}"
            )
        }
    }

    suspend fun getUserAccountTransactionsByDate() {
        try {
            val customerId = call.parameters["customerId"]
                ?: return call.respond(HttpStatusCode.BadRequest, "Missing or invalid customerId")
            val accountId = call.parameters["accountId"]
                ?: return call.respond(HttpStatusCode.BadRequest, "Missing or invalid accountId")
            val date = call.parameters["date"]
                ?: return call.respond(HttpStatusCode.BadRequest, "Missing date")
            val cursor = call.parameters["cursor"]?.takeIf { it.isNotBlank() }
            val response = Database.fetchUserAccountTransactionsByDate(
                customerId, accountId, date, cursor
            )
            call.respond(HttpStatusCode.OK, response)
        } catch (t: Throwable) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "Error: ${t.message ?: "Unknown error"}"
            )
        }
    }
}