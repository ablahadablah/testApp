package com.ablahadablah.user.lifehacktest.api

sealed class CompaniesEvent {
    data class Success(val companies: List<Company>) : CompaniesEvent()
    data class Failure(val error: Throwable) : CompaniesEvent()
}

sealed class CompanyInfoEvent {
    data class Success(val company: CompanyDescription) : CompanyInfoEvent()
    data class Failure(val error: Throwable) : CompanyInfoEvent()
}
