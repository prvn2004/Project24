package project.aio.project24.Managers.Functions.ConnectionManager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.google.android.play.core.integrity.e
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL


class ConnectionFunctions(private val context: Context) {

    val isConnectingToInternet: Boolean
        get() {
            val connectivity =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
            return false
        }
    val isURLReachable: Boolean
        get() {
            val cm =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return if (netInfo != null && netInfo.isConnected) {
                try {
                    val url = URL("51.20.89.113:80") // Insert Url
                    val urlc = url.openConnection() as HttpURLConnection
                    urlc.connectTimeout = 10 * 1000 // 10 s.
                    try {
                        urlc.connect()
                    } catch (e: SocketTimeoutException) {
                        // Handle timeout exception
                        e.printStackTrace() // Log the error for debugging
                        // Provide fallback behavior or inform the user about the connection issue
                        println("Connection timed out. Please check your network connection.")

                    } catch (e: IOException) {
                        // Handle other I/O exceptions
                        e.printStackTrace()
                        // Provide fallback behavior or inform the user about the connection issue
                        println("An error occurred while connecting to the server.")
                    }

                    if (urlc.responseCode == 200) {        // 200 = "OK" code (http connection is fine).
                        Log.wtf("Connection", "Success !")
                        true
                    } else {
                        false
                    }
                } catch (e1: MalformedURLException) {
                    false
                } catch (e: IOException) {
                    false
                }
            } else false
        }
}
