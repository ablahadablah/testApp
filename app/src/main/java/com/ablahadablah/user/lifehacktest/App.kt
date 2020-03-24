package com.ablahadablah.user.lifehacktest

import androidx.multidex.MultiDexApplication
import android.content.Context
import com.ablahadablah.user.lifehacktest.api.Api
import org.kodein.di.Kodein
import org.kodein.di.conf.KodeinGlobalAware
import org.kodein.di.conf.global
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

class App: MultiDexApplication(), KodeinGlobalAware {
    override fun onCreate() {
        super.onCreate()

        initDIC()

        appContext = applicationContext
    }

    private fun initDIC() {
        Kodein.global.mutable = true
        Kodein.global.addImport(Kodein.Module("apiModule") {
            bind<Api>() with singleton {
                Api(addrServer)
            }
        })
    }

    companion object {
        const val defaultLogTag = "MainLog"

        lateinit var appContext: Context
            private set

        const val addrServer = "http://megakohz.bget.ru/"
    }
}
