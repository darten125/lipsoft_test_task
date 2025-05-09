package com.example

import kotlinx.serialization.Serializable

@Serializable
data class GetUserTransactionsReceiveRemote(
    val customerId: String,
    val cursor: String? = null
)

@Serializable
data class GetUserAccountTransactionsReceiveRemote(
    val customerId: String,
    val accountId: String,
    val cursor: String? = null
)

@Serializable
data class GetUserTransactionsByDateReceiveRemote(
    val customerId: String,
    val date: String,
    val cursor: String? = null
)

@Serializable
data class GetUserAccountTransactionsByDateReceiveRemote(
    val customerId: String,
    val accountId: String,
    val date: String,
    val cursor: String? = null
)

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