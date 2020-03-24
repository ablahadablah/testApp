package com.ablahadablah.user.lifehacktest.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ablahadablah.user.lifehacktest.api.Api
import com.ablahadablah.user.lifehacktest.api.Company
import com.ablahadablah.user.lifehacktest.api.CompanyDescription
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.kodein.di.conf.KodeinGlobalAware
import org.kodein.di.generic.instance

class MainViewModel : ViewModel(), KodeinGlobalAware {
    private val api: Api by instance()
    
    private var companies: List<Company> = emptyList()
    private var companiesDescription: MutableMap<Long, CompanyDescription> = mutableMapOf()
    
    private var selectedCompanyStream: BehaviorSubject<CompanyDescription> 
            = BehaviorSubject.create()
    val selectedCompany: Observable<CompanyDescription>
        get() = selectedCompanyStream

    fun getAllCompanies(): Single<List<Company>> =
        if (companies.isNotEmpty()) {
            Single.just(companies)
        } else {
            api.company.getAllCompanies()
                .subscribeOn(Schedulers.io())
                .doOnSuccess { cs -> companies = cs }
        }

    fun selectCompany(companyId: Long) {
        Log.d("MainLog", "selecting a company with id: $companyId")
        val company = companiesDescription[companyId]
        
        if (company == null) {
            val d = api.company.getOneCompanyById(companyId)
                .subscribeOn(Schedulers.io())
                .subscribe({ companyDescription ->
                    if (companyDescription.isNotEmpty()) {
                        companiesDescription[companyId] = companyDescription.first()
                        selectedCompanyStream.onNext(companyDescription.first())
                    }
                }, { e ->
                    Log.e("MainLog", "error loading company", e)
                })
        } else {
            selectedCompanyStream.onNext(company)
        }
    }
}