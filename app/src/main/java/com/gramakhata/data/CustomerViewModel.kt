package com.gramakhata.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CustomerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CustomerRepository by lazy {
        val db = AppDatabase.getDatabase(application)
        CustomerRepository(db.customerDao())
    }

    val customers: StateFlow<List<Customer>> = repository.allCustomers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    suspend fun getCustomer(id: String): Customer? = repository.getCustomer(id)

    fun addCustomer(name: String, phone: String, photoUri: String?, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            val id = repository.addCustomer(name, phone, photoUri)
            onComplete(id)
        }
    }

    fun addTransaction(customerId: String, amount: Double, type: TransactionType, note: String?) {
        viewModelScope.launch {
            repository.addTransaction(customerId, amount, type, note)
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }
}
