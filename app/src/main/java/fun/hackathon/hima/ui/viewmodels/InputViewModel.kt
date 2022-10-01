package `fun`.hackathon.hima.ui.viewmodels

import `fun`.hackathon.hima.data.model.Params
import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.FireStoreService
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class InputViewModel @Inject constructor(
    private val fireStoreService: FireStoreService,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModel() {
    val postModel = mutableStateOf(PostDataModel())
    val positionState = CameraPositionState()

    fun addPost(): Boolean {
        return fireStoreService.addPost(post = postModel.value.copy(geoPoint = GeoPoint(0.0, 0.0)))
    }

    fun updateGeoPoint(latLng: LatLng) {
        postModel.value =
            postModel.value.copy(geoPoint = GeoPoint(latLng.latitude, latLng.longitude))
    }

    fun fetchLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Params.REQUEST_CODE_LOCATION
            )
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            updateGeoPoint(LatLng(it.latitude, it.longitude))
            positionState.position =
                CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 18f)
            println(it)
        }.addOnFailureListener {
            println(it)
        }.addOnCompleteListener {
            println(it)
        }
    }
}