package com.example.moil.core.network

import com.example.moil.BuildConfig
import com.example.moil.feature.auth.data.remote.AuthenticatedAuthApiService
import com.example.moil.feature.auth.data.remote.PublicAuthApiService
import com.example.moil.feature.auth.data.remote.RefreshAuthApiService
import com.example.moil.feature.group.data.remote.GroupApiService
import com.example.moil.feature.event.data.remote.EventApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.ENABLE_LOG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    @Provides
    @Singleton
    @PublicClient
    fun providePublicOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient = baseClient(loggingInterceptor).build()

    @Provides
    @Singleton
    @AuthenticatedClient
    fun provideAuthenticatedOkHttpClient(
        authorizationInterceptor: AuthorizationInterceptor,
        tokenRefreshAuthenticator: TokenRefreshAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = baseClient(loggingInterceptor)
        .addInterceptor(authorizationInterceptor)
        .authenticator(tokenRefreshAuthenticator)
        .build()

    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshOkHttpClient(
        authorizationInterceptor: AuthorizationInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = baseClient(loggingInterceptor)
        .addInterceptor(authorizationInterceptor)
        .build()

    @Provides
    @Singleton
    @PublicClient
    fun providePublicRetrofit(
        @PublicClient okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit = createRetrofit(okHttpClient, json)

    @Provides
    @Singleton
    @AuthenticatedClient
    fun provideAuthenticatedRetrofit(
        @AuthenticatedClient okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit = createRetrofit(okHttpClient, json)

    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshRetrofit(
        @RefreshClient okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit = createRetrofit(okHttpClient, json)

    @Provides
    @Singleton
    fun providePublicAuthApiService(@PublicClient retrofit: Retrofit): PublicAuthApiService = retrofit.create(PublicAuthApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthenticatedAuthApiService(@AuthenticatedClient retrofit: Retrofit): AuthenticatedAuthApiService = retrofit.create(AuthenticatedAuthApiService::class.java)

    @Provides
    @Singleton
    fun provideRefreshAuthApiService(@RefreshClient retrofit: Retrofit): RefreshAuthApiService = retrofit.create(RefreshAuthApiService::class.java)

    @Provides
    @Singleton
    fun provideGroupApiService(@AuthenticatedClient retrofit: Retrofit): GroupApiService = retrofit.create(GroupApiService::class.java)

    @Provides
    @Singleton
    fun provideEventApiService(@AuthenticatedClient retrofit: Retrofit): EventApiService = retrofit.create(EventApiService::class.java)

    private fun baseClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(BuildConfig.CONNECT_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .readTimeout(BuildConfig.CONNECT_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .writeTimeout(BuildConfig.CONNECT_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)

    private fun createRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}
