package com.example

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.*

class RequestsTests {
    private val sampleIncomeCsv = """
        transaction_id,customer_id,account_id,amount,date_time
        t1,1,101,50.0,2025-05-01T10:00:00
        t2,1,101,75.0,2025-05-02T11:30:00
        t3,2,201,20.0,2025-05-03T09:15:00
    """.trimIndent()

    private val sampleOutcomeCsv = """
        transaction_id,customer_id,account_id,amount,date_time
        o1,1,101,30.0,2025-05-01T12:00:00
        o2,2,201,10.0,2025-05-02T08:45:00
    """.trimIndent()

    @Test
    fun testAllRequests() = testApplication {

        application {
            module()
        }

        val response = client.post("/update_transactions_data") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "incomes",
                            value = sampleIncomeCsv.toByteArray(),
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"incomes\"; filename=\"incomes.csv\""
                                )
                            }
                        )
                        append(
                            key = "outcomes",
                            value = sampleOutcomeCsv.toByteArray(),
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"outcomes\"; filename=\"outcomes.csv\""
                                )
                            }
                        )
                    }
                )
            )
        }
        println("RESP1 STATUS = ${response.status}")
        println("RESP1 BODY   = ${response.bodyAsText()}")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Transactions updated", response.bodyAsText())

        val json = Json { ignoreUnknownKeys = true }

        //get_user_transactions
        val resp1: HttpResponse = client.get("/get_user_transactions?customerId=1&cursor=")
        assertEquals(HttpStatusCode.OK, resp1.status)
        val body1: TransactionsResponseRemote = json.decodeFromString(resp1.bodyAsText())
        // Должны быть транзакции с ID: t1, t2, o1
        assertEquals(3, body1.transactions.size)
        assertTrue(body1.transactions.all { it.customerId == "1" })

        //get_user_account_transactions
        val resp2: HttpResponse = client.get("/get_user_account_transactions?customerId=1&accountId=101&cursor=")
        assertEquals(HttpStatusCode.OK, resp2.status)
        val body2: TransactionsResponseRemote = json.decodeFromString(resp2.bodyAsText())
        // // Должны быть транзакции с ID: t1, t2, o1
        assertEquals(3, body2.transactions.size)
        assertTrue(body2.transactions.all { it.accountId == "101" })

        //get_user_transactions_by_date
        val resp3: HttpResponse = client.get("/get_user_transactions_by_date?customerId=1&date=2025-05-02&cursor=")
        assertEquals(HttpStatusCode.OK, resp3.status)
        val body3: TransactionsResponseRemote = json.decodeFromString(resp3.bodyAsText())
        // // Должны быть транзакции с ID: t2
        assertEquals(1, body3.transactions.size)
        assertEquals("t2", body3.transactions.first().transactionId)

        //get_user_account_transactions_by_date
        val resp4: HttpResponse = client.get("/get_user_account_transactions_by_date?customerId=1&accountId=101&date=2025-05-01&cursor=")
        assertEquals(HttpStatusCode.OK, resp4.status)
        val body4: TransactionsResponseRemote = json.decodeFromString(resp4.bodyAsText())
        // // Должны быть транзакции с ID: t1, o1
        assertEquals(2, body4.transactions.size)
        assertTrue(body4.transactions.any { it.transactionId == "t1" })
        assertTrue(body4.transactions.any { it.transactionId == "o1" })
    }
}