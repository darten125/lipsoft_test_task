package com.example

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        post("/update_transactions_data") {
            val controller = TransactionsController(call)
            controller.updateTransactionsData()
        }
        get("/get_user_transactions") {
            val controller = TransactionsController(call)
            controller.getUserTransactions()
        }
        get("/get_user_account_transactions") {
            val controller = TransactionsController(call)
            controller.getUserAccountTransactions()
        }
        get("/get_user_transactions_by_date") {
            val controller = TransactionsController(call)
            controller.getUserTransactionsByDate()
        }
        get("/get_user_account_transactions_by_date") {
            val controller = TransactionsController(call)
            controller.getUserAccountTransactionsByDate()
        }
    }
}
