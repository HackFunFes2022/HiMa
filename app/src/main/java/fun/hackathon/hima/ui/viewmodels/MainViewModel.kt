package `fun`.hackathon.hima.ui.viewmodels

import `fun`.hackathon.hima.data.model.Params
import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.model.Posts
import `fun`.hackathon.hima.data.services.FireStoreService
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fireStoreService: FireStoreService,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : ViewModel() {

    private val collection = fireStoreService.getCollection()

    private val _nowLocationState = MutableStateFlow(NowLocationState())
    var nowLocationState = _nowLocationState.asStateFlow()
    private val _mainUiState = MutableStateFlow(MainUiState())
    var mainUiState = _mainUiState.asStateFlow()

    fun startFetch(context: Context) {
        _mainUiState.value = MainUiState(loading = true)
        startFetchCollection()
        startFetchLocation(context)
    }

    private fun startFetchCollection() {
        collection.addSnapshotListener { snapshot, e ->
            if (snapshot == null) {
                return@addSnapshotListener
            }
            if (e != null) {
                _mainUiState.value = MainUiState(error = e)
            }
            val postData: List<Posts> = snapshot.documents.map {
                val data = it.data
                if (data != null) {
                    val post = PostDataModel.fromMap(data)
                    Posts.fromPostDataModel(it.id, post = post)
                } else {
                    Posts()
                }
            }
            _mainUiState.value = MainUiState(postData = postData)
        }
    }

    private fun startFetchLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Params.REQUEST_CODE_LOCATION
            )
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            _nowLocationState.value = NowLocationState(location = it)
            Timber.d("lat: ${it.latitude}, lng: ${it.longitude}")
        }
    }
}

data class NowLocationState(
    val location: Location = Location("")
)

data class MainUiState(
    val loading: Boolean = false,
    val postData: List<Posts> = emptyList(),
    val error: Exception? = null
)