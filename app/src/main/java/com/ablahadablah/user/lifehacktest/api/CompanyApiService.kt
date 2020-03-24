package com.ablahadablah.user.lifehacktest.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CompanyApiService {
    @GET("/test_task/test.php")
    fun getAllCompanies(): Single<List<Company>>
    
    @GET("/test_task/test.php")
    fun getOneCompanyById(
        @Query("id") companyId: Long
    ): Single<List<CompanyDescription>>
}
