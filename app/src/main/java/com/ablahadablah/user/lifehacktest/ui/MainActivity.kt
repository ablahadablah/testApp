package com.ablahadablah.user.lifehacktest.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.ablahadablah.user.lifehacktest.R
import com.ablahadablah.user.lifehacktest.api.Api
import com.ablahadablah.user.lifehacktest.viewmodel.MainViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

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
        
        viewModel.selectedCompany
            .subscribe({
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                        android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.mainFragmentContainer, CompanyInfoFragment(), "info-fragment")
                    .addToBackStack(null)
                    .commit()
            }, { e ->
                Log.e("MainLog", "Error switching to info fragment", e)
            })
            .let(disposable::add)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
