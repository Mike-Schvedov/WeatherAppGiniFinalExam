package com.mikeschvedov.weatherappginifinalexam.di


import android.content.Context
import android.net.ConnectivityManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mikeschvedov.weatherappginifinalexam.BuildConfig.AMBEE_API_KEY
import com.mikeschvedov.weatherappginifinalexam.BuildConfig.AMBEE_BASE_URL
import com.mikeschvedov.weatherappginifinalexam.data.network.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    private const val CONTENT_HEADER = "Content-type"
    private const val CONTENT_TYPE = "application/json"
    private const val AUTHORIZATION_HEADER = "x-api-key"

    @Provides
    fun provideGsonFactory(): Converter.Factory = GsonConverterFactory.create()

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY //full log
    }

    @Provides
    fun provideAuthorizationInterceptor() = Interceptor { chain ->
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .addHeader(CONTENT_HEADER, CONTENT_TYPE)
            .addHeader(AUTHORIZATION_HEADER, AMBEE_API_KEY).build()
        chain.proceed(newRequest)
    }

    @Provides
    fun provideOKHTTPClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor).build()

    @Provides
    fun provideRetrofit(
        httpClient: OkHttpClient,
        gsonConverterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(AMBEE_BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .build()

    @Provides
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)

    @Provides
    fun provideFusedLocation(@ApplicationContext appContext: Context): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)


    @Provides
    fun provideConnectivityManager(@ApplicationContext appContext: Context): ConnectivityManager {
        return  appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }


}