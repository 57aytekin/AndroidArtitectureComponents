package com.example.yeniappwkotlin

import android.app.Application
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.ui.activity.auth.AuthViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class MVVMApplication: Application(), KodeinAware{
    override val kodein = Kodein.lazy {
        import(androidXModule(this@MVVMApplication))

        //bind() from singleton { NoConnectionInterceptor(instance()) }
        bind() from singleton { MyApi(instance()) }
        bind() from singleton { AppDatabase(instance()) }
        bind() from singleton { UserRepository(instance(), instance()) }
        bind() from singleton {
            AuthViewModelFactory(
                instance()
            )
        }
    }
}