package com.gramakhata.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

class CustomerRepository(private val dao: CustomerDao) {

    val allCustomers: Flow<List<Customer>> = dao.getAllCustomers()

    suspend fun getCustomer(id: String): Customer? = dao.getCustomerById(id)

    suspend fun addCustomer(name: String, phone: String, photoUri: String?): String {
        val id = UUID.randomUUID().toString()
        dao.insertCustomer(Customer(id = id, name = name, phone = phone, photoUri = photoUri))
        return id
    }

    suspend fun addTransaction(customerId: String, amount: Double, type: TransactionType, note: String?) {
        val customer = dao.getCustomerById(customerId) ?: return
        val txn = Transaction(
            id = UUID.randomUUID().toString(),
            amount = amount,
            type = type,
            note = note,
            date = LocalDate.now().toString()
        )
        val updated = customer.copy(transactions = customer.transactions + txn)
        dao.updateCustomer(updated)
    }

    suspend fun deleteCustomer(customer: Customer) = dao.deleteCustomer(customer)
}
