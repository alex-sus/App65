package ru.yodata.app65.di.viewmodel_factory

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.yodata.library.viewmodel.ViewModelFactory

@Module
abstract class ViewModelFactoryModule {

    //@Singleton
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}