package com.example.drunkenautopilot.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drunkenautopilot.models.Episode

class RouteViewModelFactory(private val application: Application, private val episode: Episode) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RouteViewModel(application, episode) as T
    }

}