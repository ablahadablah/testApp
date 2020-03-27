package com.ablahadablah.user.lifehacktest.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.ablahadablah.user.lifehacktest.R
import com.ablahadablah.user.lifehacktest.api.CompanyInfoEvent
import com.ablahadablah.user.lifehacktest.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.JsonEncodingException
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    } 
    private val disposable = CompositeDisposable()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.mainFragmentContainer, CompaniesListFragment())
            .commit()
        
        viewModel.selectedCompanyEvent
            .subscribe({ companyEvent ->
                when (companyEvent) {
                    is CompanyInfoEvent.Success -> {
                        startCompanyInfoFragment()
                    }
                    is CompanyInfoEvent.Failure -> {
                        showErrorMessage(companyEvent.error)
                    }
                }
            }, { e ->
                Log.e("MainLog", "Unknown error", e)
                Snackbar.make(
                    mainFragmentContainer,
                    R.string.unknown_error,
                    Snackbar.LENGTH_LONG
                ).show()
            })
            .let(disposable::add)
    }

    private fun showErrorMessage(e: Throwable) {
        val errorMsgId = when (e) {
            is UnknownHostException -> {
                R.string.connection_error
            }
            is JsonEncodingException -> {
                R.string.company_json_parsing_failure
            }
            else -> {
                Log.d("MainLog", "Failed to download company info =\\")
                R.string.company_info_download_failure
            }
        }

        Snackbar.make(
            mainFragmentContainer,
            errorMsgId,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun startCompanyInfoFragment() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right
            )
            .replace(R.id.mainFragmentContainer, CompanyInfoFragment(), "info-fragment")
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
