//package com.example.athanapp.ui
//
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewmodel.CreationExtras
//import androidx.lifecycle.viewmodel.initializer
//import androidx.lifecycle.viewmodel.viewModelFactory
//import com.example.athanapp.MainActivity
//import com.example.athanapp.ui.screens.AthanViewModel
//
//object AppViewModelProvider {
//
//    val Factory = viewModelFactory {
//        initializer {
//            AthanViewModel(
//                mainActivity().appContainer.prayersRepository
//            )
//        }
//    }
//}
//
//
//fun CreationExtras.mainActivity(): MainActivity =
//    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainActivity)