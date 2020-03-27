package com.ablahadablah.user.lifehacktest.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.lifecycle.ViewModelProvider

import com.ablahadablah.user.lifehacktest.R
import com.ablahadablah.user.lifehacktest.api.CompanyInfoEvent
import com.ablahadablah.user.lifehacktest.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_company_info.*

class CompanyInfoFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(activity!!).get(MainViewModel::class.java)
    }
    private val disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_company_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        companyInfoDescription.movementMethod = ScrollingMovementMethod()
        
    }

    override fun onResume() {
        super.onResume()

        viewModel.selectedCompanyEvent
            .subscribe({ companyEvent ->
                if (companyEvent is CompanyInfoEvent.Success) {
                    val company = companyEvent.company
                    companyInfoName.text = company.name

                    if (company.description.isNotEmpty()) {
                        companyInfoDescription.text = company.description
                    } else {
                        companyInfoDescription.isGone = true
                    }

                    if (company.www.isNotEmpty()) {
                        companyInfoWebsite.text = company.www
                        companyInfoWebsite.setOnClickListener {
                            val url = if (company.www.startsWith("http")) {
                                company.www
                            } else {
                                "http:${company.www}"
                            }
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW, Uri.parse(url)
                                )
                            )
                        }
                    } else {
                        companyInfoWebsite.isGone = true
                    }

                    if (company.lat != 0.0 && company.lon != 0.0) {
                        companyInfoOpenMapButt.setOnClickListener {
                            val uri = Uri.parse("geo:${company.lat},${company.lon}")
                            startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    } else {
                        companyInfoOpenMapButt.isGone = true
                    }

                    if (company.phone.isNotEmpty()) {
                        companyInfoPhone.text = company.phone
                        companyInfoPhone.setOnClickListener {
                            startActivity(
                                Intent(
                                    Intent.ACTION_DIAL, Uri.parse("tel:${company.phone}")
                                )
                            )
                        }
                    } else {
                        companyInfoWebsite.isGone = true
                    }

                    if (company.img.isNotEmpty()) {
                        Glide.with(this)
                            .load("http://megakohz.bget.ru/test_task/${company.img}")
                            .into(companyInfoImageView)
                    } else {
                        companyInfoImageView.isGone = true
                    }
                }
            }, { e ->
                Log.e("MainLog", "Error", e)
            })
            .let { d -> disposable.add(d)}
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }
}
