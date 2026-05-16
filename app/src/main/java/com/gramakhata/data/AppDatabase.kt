package com.gramakhata.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

@Database(entities = [Customer::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "grama_khata_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed initial data
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.customerDao()?.let { dao ->
                                    val today = LocalDate.now().toString()
                                    val yesterday = LocalDate.now().minusDays(3).toString()
                                    dao.insertCustomer(
                                        Customer(
                                            id = UUID.randomUUID().toString(),
                                            name = "Ramesh Kumar",
                                            phone = "9876543210",
                                            transactions = listOf(
                                                Transaction(UUID.randomUUID().toString(), 500.0, TransactionType.CREDIT, "Rice 5kg", yesterday),
                                                Transaction(UUID.randomUUID().toString(), 200.0, TransactionType.CREDIT, "Oil", today)
                                            )
                                        )
                                    )
                                    dao.insertCustomer(
                                        Customer(
                                            id = UUID.randomUUID().toString(),
                                            name = "Priya Devi",
                                            phone = "9876501234",
                                            transactions = listOf(
                                                Transaction(UUID.randomUUID().toString(), 1200.0, TransactionType.CREDIT, "Groceries", yesterday),
                                                Transaction(UUID.randomUUID().toString(), 300.0, TransactionType.PAYMENT, null, today)
                                            )
                                        )
                                    )
                                    dao.insertCustomer(
                                        Customer(
                                            id = UUID.randomUUID().toString(),
                                            name = "Suresh Babu",
                                            phone = "9123456780",
                                            transactions = listOf(
                                                Transaction(UUID.randomUUID().toString(), 150.0, TransactionType.CREDIT, "Sugar", today)
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
