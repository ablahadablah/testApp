package com.ablahadablah.user.lifehacktest.viewmodel

import androidx.lifecycle.ViewModel
import com.ablahadablah.user.lifehacktest.api.Api
import com.ablahadablah.user.lifehacktest.api.Company
import com.ablahadablah.user.lifehacktest.api.CompanyApiService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.kodein.di.conf.KodeinGlobalAware
import org.kodein.di.generic.instance

class MainViewModel : ViewModel(), KodeinGlobalAware {
    private val api: Api by instance()
    
    private var companies: List<Company> = emptyList()
    
    fun getAllCompanies(): Single<List<Company>> =
        if (companies.isNotEmpty()) {
            Single.just(companies)
        } else {
            api.company.getAllCompanies()
                .subscribeOn(Schedulers.io())
                .doOnSuccess { cs -> companies = cs }
        }
}