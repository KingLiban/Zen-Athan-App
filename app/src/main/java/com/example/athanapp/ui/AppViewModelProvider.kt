package com.example.athanapp.ui

//
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewmodel.CreationExtras
//import androidx.lifecycle.viewmodel.initializer
//import androidx.lifecycle.viewmodel.viewModelFactory
//import com.example.athanapp.AthanApplication
//import com.example.athanapp.MainActivity
//import com.example.athanapp.ui.screens.AthanViewModel
//import com.example.athanapp.ui.screens.PreferencesViewModel
//
//object AppViewModelProvider {
//
//    val Factory = viewModelFactory {
//        initializer {
//            AthanViewModel(
//                athanApplication().appContainer.prayersRepository
//            )
////            PreferencesViewModel(
////                athanApplication().userPreferencesRepository
////            )
//        }
//    }
//}
//
//
//fun CreationExtras.athanApplication(): AthanApplication =
//    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AthanApplication)