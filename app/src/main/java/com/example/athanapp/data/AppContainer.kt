package com.example.athanapp.data

interface AppContainer {
    val athanObjectRepository: AthanObjectRepository
}

class DefaultAppContainer : AppContainer {

    private var _coarseLocation: String = "Not available"

    override val athanObjectRepository: AthanObjectRepository
        get() = TODO("Not yet implemented")

//    val coarseLocation: String
//        get() = _coarseLocation

    fun updateLocation(location: String) {
        _coarseLocation = location
    }
}