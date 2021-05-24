package ru.yodata.app65.di.map.route

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.yodata.app65.di.viewmodel_factory.ViewModelKey
import ru.yodata.library.viewmodel.RouteMapViewModel

@Module
abstract class RouteMapViewModelModule {
    @RouteMapScope
    @Binds
    @IntoMap
    @ViewModelKey(RouteMapViewModel::class)
    abstract fun bindRouteMapViewModel(viewModel: RouteMapViewModel): ViewModel

}