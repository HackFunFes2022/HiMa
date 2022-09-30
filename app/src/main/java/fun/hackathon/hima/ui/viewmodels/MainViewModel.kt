package `fun`.hackathon.hima.ui.viewmodels

import `fun`.hackathon.hima.data.services.FireStoreService
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(
    private  val fireStoreService: FireStoreService,
):ViewModel() {
    val collection=fireStoreService.getCollection()
}