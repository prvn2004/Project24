//package project.aio.project24.Managers.Functions.ConnectionManager
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import kotlinx.coroutines.delay
//import androidx.lifecycle.MutableLiveData
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import project.aio.project24.Api.Sockets.SocketManager
//
//class ConnectionManager(private val connectionService: ConnectionService, private val socketManager: SocketManager) {
//
//    private val _isConnected = MutableLiveData<Boolean>()
//    val isConnected: LiveData<Boolean> get() = _isConnected
//
//    init {
//        observeConnectionStatus()
//    }
//
//    private fun observeConnectionStatus() {
//        CoroutineScope(Dispatchers.IO).launch {
//            while (true) {
//                val currentStatus = connectionService.isConnected && connectionService.isUrlReachable
//                if (_isConnected.value != currentStatus) {
//                    _isConnected.postValue(currentStatus)
////                    if (currentStatus) {
////                        try {
////                            socketManager.connect()
////                        } catch (e: Exception) {
////                            Log.e("ConnectionManager", "Failed to connect to socket", e)
////                        }
////                    }
//                }
//                delay(10000) // Check every 5 seconds
//            }
//        }
//    }
//}