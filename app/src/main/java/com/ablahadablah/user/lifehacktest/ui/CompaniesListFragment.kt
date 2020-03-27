package com.ablahadablah.user.lifehacktest.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.ablahadablah.user.lifehacktest.R
import com.ablahadablah.user.lifehacktest.api.CompaniesEvent
import com.ablahadablah.user.lifehacktest.api.Company
import com.ablahadablah.user.lifehacktest.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.squareup.moshi.JsonEncodingException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_companies_list.*
import java.net.UnknownHostException

class CompaniesListFragment : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(activity!!).get(MainViewModel::class.java)
    }
    private val companiesListAdapter = MapSelectionListAdapter()
    private val disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_companies_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        companiesList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity!!)
            adapter = companiesListAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getAllCompanies()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ companiesEvent ->
                when (companiesEvent) {
                    is CompaniesEvent.Success -> {
                        companiesListAdapter.dataSet = companiesEvent.companies
                        companiesListAdapter.notifyDataSetChanged()
                    }
                    is CompaniesEvent.Failure -> {
                        showErrorMessage(companiesEvent.error)   
                    }
                }
            }, { e ->
                showErrorMessage(e)
            })
            .let { d -> disposable.add(d) }
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
            view!!,
            errorMsgId,
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView by lazy {
            view.findViewById<MaterialTextView>(R.id.companyNameTextView)
        }
        private val imageView by lazy {
            view.findViewById<AppCompatImageView>(R.id.companyImageView)
        }

        var companyId: Long = 0
        var companyName: String = ""
            set(value) {
                field = value
                textView.text = field
            }

        init {
            textView.setOnClickListener {
                viewModel.selectCompany(companyId)
            }
        }

        fun init(company: Company) {
            companyId = company.id
            companyName = company.name
            Glide.with(this@CompaniesListFragment)
                .load("http://megakohz.bget.ru/test_task/${company.img}")
                .into(imageView)
        }
    }

    inner class MapSelectionListAdapter : RecyclerView.Adapter<ViewHolder>() {
        var dataSet = listOf<Company>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.recycler_companies_list_item, parent, false)
                .let { ViewHolder(it) }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.comapanyId = dataSet[position].id
//            holder.companyName = dataSet[position].name
            holder.init(dataSet[position])
        }

        override fun getItemCount() = dataSet.size
    }
}
