package com.example

import java.sql.Connection
import java.sql.DriverManager
import java.time.format.DateTimeFormatter

object Database {
    private const val PAGE_SIZE = 20
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private const val JDBC_URL : String = "jdbc:duckdb:./transactions.db"
    private val connection: Connection = DriverManager.getConnection(JDBC_URL)

    fun init() {
        connection.autoCommit = false

        connection.createStatement().use { statement ->
            statement.executeUpdate("DROP TABLE IF EXISTS transactions")
            statement.executeUpdate("""
                CREATE TABLE transactions (
                    transaction_id VARCHAR,
                    customer_id VARCHAR,
                    account_id VARCHAR,
                    amount DOUBLE,
                    date_time TIMESTAMP
                )
            """.trimIndent())
            statement.executeUpdate("""
                CREATE INDEX idx_cust_date 
                ON transactions(customer_id, date_time)
            """.trimIndent())
        }
        connection.commit()
    }

    fun importCsv(path: String) {
        connection.createStatement().use { statement ->
            statement.execute(
                "COPY transactions FROM '$path' (AUTO_DETECT TRUE, HEADER TRUE)"
            )
        }
        connection.commit()
    }

    fun fetchUserTransactions(
        customerId: String,
        cursor: String?
    ): TransactionsResponseRemote {
        val sql = buildString {
            append("""
                SELECT transaction_id, customer_id, account_id, amount, date_time
                FROM transactions
                WHERE customer_id = '$customerId'
            """.trimIndent())
            if (cursor != null) append(" AND date_time < TIMESTAMP '$cursor'")
            append(" ORDER BY date_time DESC LIMIT $PAGE_SIZE")
        }

        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(sql)

            val transactionsList = mutableListOf<TransactionRemote>()
            var lastCursor: String? = null

            while (resultSet.next()) {
                val dateTime = resultSet.getTimestamp("date_time")
                    .toLocalDateTime()
                    .format(dateTimeFormatter)

                transactionsList += TransactionRemote(
                    transactionId = resultSet.getString("transaction_id"),
                    customerId    = resultSet.getString("customer_id"),
                    accountId     = resultSet.getString("account_id"),
                    amount        = resultSet.getDouble("amount"),
                    dateTime      = dateTime
                )
                lastCursor = dateTime
            }
            return TransactionsResponseRemote(transactions = transactionsList, nextCursor = lastCursor)
        }
    }

    fun fetchUserAccountTransactions(
        customerId: String,
        accountId: String,
        cursor: String?
    ): TransactionsResponseRemote {
        val sql = buildString {
            append("""
                SELECT transaction_id, customer_id, account_id, amount, date_time
                FROM transactions
                WHERE customer_id = '$customerId' 
                AND account_id = '$accountId'
            """.trimIndent())
            if (cursor != null) append(" AND date_time < TIMESTAMP '$cursor'")
            append(" ORDER BY date_time DESC LIMIT $PAGE_SIZE")
        }

        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(sql)

            val transactionsList = mutableListOf<TransactionRemote>()
            var lastCursor: String? = null

            while (resultSet.next()) {
                val dateTime = resultSet.getTimestamp("date_time")
                    .toLocalDateTime()
                    .format(dateTimeFormatter)

                transactionsList += TransactionRemote(
                    transactionId = resultSet.getString("transaction_id"),
                    customerId    = resultSet.getString("customer_id"),
                    accountId     = resultSet.getString("account_id"),
                    amount        = resultSet.getDouble("amount"),
                    dateTime      = dateTime
                )
                lastCursor = dateTime
            }
            return TransactionsResponseRemote(transactions = transactionsList, nextCursor = lastCursor)
        }
    }

    fun fetchUserTransactionsByDate(
        customerId: String,
        date: String,
        cursor: String?
    ): TransactionsResponseRemote {
        val from = "$date 00:00:00"
        val to   = "$date 23:59:59"

        val sql = buildString {
            append("""
                SELECT transaction_id, customer_id, account_id, amount, date_time
                FROM transactions
                WHERE customer_id = '$customerId'
                AND date_time >= TIMESTAMP '$from'
                AND date_time <= TIMESTAMP '$to'
            """.trimIndent())
            if (cursor != null) append(" AND date_time < TIMESTAMP '$cursor'")
            append(" ORDER BY date_time DESC LIMIT $PAGE_SIZE")
        }

        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(sql)

            val transactionsList = mutableListOf<TransactionRemote>()
            var lastCursor: String? = null

            while (resultSet.next()) {
                val dateTime = resultSet.getTimestamp("date_time")
                    .toLocalDateTime()
                    .format(dateTimeFormatter)

                transactionsList += TransactionRemote(
                    transactionId = resultSet.getString("transaction_id"),
                    customerId    = resultSet.getString("customer_id"),
                    accountId     = resultSet.getString("account_id"),
                    amount        = resultSet.getDouble("amount"),
                    dateTime      = dateTime
                )
                lastCursor = dateTime
            }
            return TransactionsResponseRemote(transactions = transactionsList, nextCursor = lastCursor)
        }
    }

    fun fetchUserAccountTransactionsByDate(
        customerId: String,
        accountId: String,
        date: String,
        cursor: String?
    ): TransactionsResponseRemote {
        val from = "$date 00:00:00"
        val to   = "$date 23:59:59"

        val sql = buildString {
            append("""
                SELECT transaction_id, customer_id, account_id, amount, date_time
                FROM transactions
                WHERE customer_id = '$customerId' AND account_id = '$accountId'
                AND date_time >= TIMESTAMP '$from'
                AND date_time <= TIMESTAMP '$to'
            """.trimIndent())
            if (cursor != null) append(" AND date_time < TIMESTAMP '$cursor'")
            append(" ORDER BY date_time DESC LIMIT $PAGE_SIZE")
        }

        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(sql)

            val transactionsList = mutableListOf<TransactionRemote>()
            var lastCursor: String? = null

            while (resultSet.next()) {
                val dateTime = resultSet.getTimestamp("date_time")
                    .toLocalDateTime()
                    .format(dateTimeFormatter)

                transactionsList += TransactionRemote(
                    transactionId = resultSet.getString("transaction_id"),
                    customerId    = resultSet.getString("customer_id"),
                    accountId     = resultSet.getString("account_id"),
                    amount        = resultSet.getDouble("amount"),
                    dateTime      = dateTime
                )
                lastCursor = dateTime
            }
            return TransactionsResponseRemote(transactions = transactionsList, nextCursor = lastCursor)
        }
    }
}