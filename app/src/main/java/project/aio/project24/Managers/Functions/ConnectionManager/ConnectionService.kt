package project.aio.project24.Managers.Functions.ConnectionManager

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class ConnectionService : Service() {

    var isConnected: Boolean = false
        private set
    var isUrlReachable: Boolean = false
        private set

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnectedT: LiveData<Boolean> get() = _isConnected

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            val connectionFunctions = ConnectionFunctions(applicationContext)
            while (isActive) {
                isConnected = connectionFunctions.isConnectingToInternet
                isUrlReachable = connectionFunctions.isURLReachable
                Log.d("testing", "${isConnected} + ${isUrlReachable}")

                val currentStatus = isConnected && isUrlReachable
//                if (_isConnected.value != currentStatus) {
                _isConnected.postValue(true)
//                }

                delay(5000) // Check every 10 seconds
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Cancel the job when the service is destroyed
    }
}