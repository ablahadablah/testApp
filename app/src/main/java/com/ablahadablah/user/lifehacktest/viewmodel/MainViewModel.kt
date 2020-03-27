package com.ablahadablah.user.lifehacktest.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ablahadablah.user.lifehacktest.api.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.kodein.di.conf.KodeinGlobalAware
import org.kodein.di.generic.instance

class MainViewModel : ViewModel(), KodeinGlobalAware {
    private val api: Api by instance()
    
    private var companies: List<Company> = emptyList()
    private var companiesDescription: MutableMap<Long, CompanyDescription> = mutableMapOf()
    
    private val disposable = CompositeDisposable()
    
//    var selectedCompany: CompanyDescription = CompanyDescription()
//        private set
    
    private var selectedCompanyStream: BehaviorSubject<CompanyInfoEvent> 
            = BehaviorSubject.create()
    val selectedCompanyEvent: Observable<CompanyInfoEvent>
        get() = selectedCompanyStream

    fun getAllCompanies(): Single<CompaniesEvent> =
        if (companies.isNotEmpty()) {
            Single.just(CompaniesEvent.Success(companies))
        } else {
            api.company.getAllCompanies()
                .subscribeOn(Schedulers.io())
                .doOnSuccess { cs -> companies = cs }
                .map { cs -> CompaniesEvent.Success(cs) }
        }

    fun selectCompany(companyId: Long) {
        Log.d("MainLog", "selecting a company with id: $companyId")
        val company = companiesDescription[companyId]
        
        if (company == null) {
            api.company.getOneCompanyById(companyId)
                .subscribeOn(Schedulers.io())
                .subscribe({ companyDescription ->
                    if (companyDescription.isNotEmpty()) {
                        companiesDescription[companyId] = companyDescription.first()
                        selectedCompanyStream.onNext(
                            CompanyInfoEvent.Success(companyDescription.first()))
                    }
                }, { e ->
                    Log.e("MainLog", "error loading company", e)
                    selectedCompanyStream.onNext(
                        CompanyInfoEvent.Failure(e))
                })
                .let(disposable::add)
        } else {
            selectedCompanyStream.onNext(
                CompanyInfoEvent.Success(company))
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}