package com.nishantpardamwar.unnamed.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nishantpardamwar.unnamed.NetworkClient
import com.nishantpardamwar.unnamed.repositories.Repository

object VMFactory : ViewModelProvider.Factory {
    private lateinit var context: Context
    private lateinit var repository: Repository
    fun init(context: Context) {
        this.context = context
        repository = Repository(context, NetworkClient)
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainActivityViewModel::class.java -> MainActivityViewModel(repository) as T
            else -> throw UnsupportedOperationException("class ${modelClass.name} not supported")
        }
    }
}