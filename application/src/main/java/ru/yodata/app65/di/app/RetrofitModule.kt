package ru.yodata.app65.di.app

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.yodata.library.data.api.YandexGeocoderApi
import ru.yodata.library.utils.Constants.BASE_API_URL
import javax.inject.Singleton

@Module
class RetrofitModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideYandexGeocoderApi(gson: Gson): YandexGeocoderApi =
            Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(YandexGeocoderApi::class.java)
}