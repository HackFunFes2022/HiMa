package `fun`.hackathon.hima.ui.viewmodels

import `fun`.hackathon.hima.data.model.Params
import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.FireStoreService
import `fun`.hackathon.hima.util.toLatLng
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class InputViewModel @Inject constructor(
    private val fireStoreService: FireStoreService,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModel() {
    val postModel = mutableStateOf(PostDataModel())
    val positionState = mutableStateOf(CameraPositionState())

    private var _imageState = MutableStateFlow(ImageState())
    val imageState get() = _imageState.asStateFlow()

    fun addPost(): Boolean {
        if (_imageState.value.data != null) {
            val baos = ByteArrayOutputStream()
            _imageState.value.data!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
        }
        return fireStoreService.addPost(post = postModel.value)
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
            updateGeoPoint(it.toLatLng())
            positionState.value =
                CameraPositionState(CameraPosition.fromLatLngZoom(it.toLatLng(), 18f))
            Timber.d("$it")
        }.addOnFailureListener {
            Timber.d("$it")
        }.addOnCompleteListener {
            Timber.d("$it")
        }
    }

    fun setImage(bitmap: Bitmap) {
        try {
            _imageState.value = _imageState.value.copy(data = bitmap)
        } catch (e: Exception) {
            _imageState.value = _imageState.value.copy(error = e)
        }
    }

    fun deleteImage() {
        try {
            _imageState.value = _imageState.value.copy(data = null)
        } catch (e: Exception) {
            _imageState.value = _imageState.value.copy(error = e)
        }
    }
}

data class ImageState(
    val data: Bitmap? = null,
    val error: Exception? = null
)