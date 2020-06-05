package com.example.yeniappwkotlin.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.HandlerThread
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.NoInternetException
import com.example.yeniappwkotlin.util.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.Response
import java.util.logging.Handler

@Suppress("DEPRECATION")
class NetworkConnectionInterceptor(
    val context: Context
) : Interceptor{
    private val aplicationContext = context.applicationContext
    override fun intercept(chain: Interceptor.Chain): Response {
        if(!isInternetAvailable()){
            Coroutines.main { context.toast("Lütfen internet bağlantınızı kontrol edin") }
            throw NoInternetException("Internet bağlantınız bulunmamakta")
        }
        return chain.proceed(chain.request())
    }

    private fun isInternetAvailable(): Boolean {
        var result = false
        val connetctivityManager = aplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as
                ConnectivityManager?
        connetctivityManager?.let {
            it.getNetworkCapabilities(connetctivityManager.activeNetwork)?.apply {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        }
        return result
    }
}