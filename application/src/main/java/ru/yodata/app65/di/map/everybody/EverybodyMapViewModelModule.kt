package ru.yodata.app65.di.map.everybody

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.yodata.app65.di.viewmodel_factory.ViewModelKey
import ru.yodata.library.viewmodel.EverybodyMapViewModel

@Module
abstract class EverybodyMapViewModelModule {
    @EverybodyMapScope
    @Binds
    @IntoMap
    @ViewModelKey(EverybodyMapViewModel::class)
    abstract fun bindEverybodyMapViewModel(viewModel: EverybodyMapViewModel): ViewModel
}