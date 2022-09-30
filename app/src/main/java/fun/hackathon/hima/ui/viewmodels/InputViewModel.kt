package `fun`.hackathon.hima.ui.viewmodels

import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.FireStoreService
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class InputViewModel @Inject constructor(
    private  val fireStoreService: FireStoreService,
    private  val fusedLocationProviderClient: FusedLocationProviderClient
) :ViewModel() {
    val postModel = mutableStateOf(PostDataModel())
    val positionState= CameraPositionState()
    val latLngState= mutableStateOf(LatLng(0.0,0.0))
//    var position= MutableStateFlow(LatLng(0.0,0.0))
//    val locationRequest = LocationRequest.create().setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//    .setFastestInterval(5000)
//    .setInterval(10000)
//    val lcb= object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            for (location in locationResult.locations) {
//                // 緯度の表示
//                val str1 = " Latitude:" + location.latitude
//                println(location)
//                // 経度の表示
//                val str2 = " Longitude:" + location.longitude
//                position.value= LatLng(location.latitude,location.longitude)
//            }
//        }
//    }

    fun addPost() {
        fireStoreService.addPost(post = postModel.value.copy(geoPoint = GeoPoint(0.0, 0.0)))
    }
    fun fetchLocation(context: Context){
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            latLngState.value= LatLng(it.latitude,it.longitude)
            positionState.position=CameraPosition.fromLatLngZoom(LatLng(it.latitude,it.longitude),18f)
            println(it)
        }.addOnFailureListener{
            println(it)
        }.addOnCompleteListener{
            println(it)
        }
    }
}