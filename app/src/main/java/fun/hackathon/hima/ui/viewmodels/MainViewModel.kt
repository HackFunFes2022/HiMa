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
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fireStoreService: FireStoreService,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : ViewModel() {

    private val collection = fireStoreService.getCollection()

    var nowLocationState by mutableStateOf(NowLocationState())
    var mainUiState by mutableStateOf(MainUiState())

    fun startFetch(context: Context) {
        startFetchCollection()
        startFetchLocation(context)
    }

    private fun startFetchCollection() {
        collection.addSnapshotListener { snapshot, e ->
            if (snapshot == null) {
                return@addSnapshotListener
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
            mainUiState = mainUiState.copy(postData = postData)
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
            nowLocationState = nowLocationState.copy(location = it)
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