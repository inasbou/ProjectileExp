package org.example.project


import org.example.project.MainViewModel

object AppContainer {
    val mainViewModel: MainViewModel by lazy {
        MainViewModel()
    }
}