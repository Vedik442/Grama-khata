package com.gramakhata.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

enum class TransactionType { CREDIT, PAYMENT }

data class Transaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val note: String? = null,
    val date: String
)

@Entity(tableName = "customers")
@TypeConverters(Converters::class)
data class Customer(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val photoUri: String? = null,
    val transactions: List<Transaction> = emptyList()
)

fun Customer.balance(): Double =
    transactions.sumOf { if (it.type == TransactionType.CREDIT) it.amount else -it.amount }

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTransactionList(value: List<Transaction>): String =
        gson.toJson(value)

    @TypeConverter
    fun toTransactionList(value: String): List<Transaction> {
        val type = object : TypeToken<List<Transaction>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}
