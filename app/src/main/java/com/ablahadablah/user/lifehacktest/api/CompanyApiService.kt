package com.ablahadablah.user.lifehacktest.api

import io.reactivex.Single
import retrofit2.http.GET

interface CompanyApiService {
    @GET("/test_task/test.php")
    fun getAllCompanies(): Single<List<Company>>
}
