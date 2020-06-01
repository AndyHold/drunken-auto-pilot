package com.example.drunkenautopilot.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drunkenautopilot.models.Route

class PointViewModelFactory(private val application: Application, private val route: Route) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PointViewModel(application, route) as T
    }
}