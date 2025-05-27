package com.example.voice_moderation.data.remote

import javax.inject.Inject

class ApiClient @Inject constructor() {
    private val okHttpClient = RetrofitHelper.createOkHttpClient()
    private val retrofit = RetrofitHelper.createRetrofit(okHttpClient)

    val monitoringApiService: MonitoringApiService = retrofit.create(MonitoringApiService::class.java)
}