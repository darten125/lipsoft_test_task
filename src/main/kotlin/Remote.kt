package com.example

import kotlinx.serialization.Serializable

@Serializable
data class TransactionRemote(
    val transactionId: String,
    val customerId: String,
    val accountId: String,
    val amount: Double,
    val dateTime: String
)

@Serializable
data class TransactionsResponseRemote(
    val transactions: List<TransactionRemote>,
    val nextCursor: String?
)