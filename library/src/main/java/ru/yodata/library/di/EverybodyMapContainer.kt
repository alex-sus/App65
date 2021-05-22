package ru.yodata.library.di

import ru.yodata.library.view.map.EverybodyMapFragment

interface EverybodyMapContainer {
    fun inject(everybodyMapFragment: EverybodyMapFragment)
}