package `fun`.hackathon.hima.ui.viewmodels

import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.FireStoreInputScreenInterface
import `fun`.hackathon.hima.data.services.FireStoreService
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InputViewModel @Inject constructor(
    private  val fireStoreService: FireStoreService
) :ViewModel() {
    val postModel = mutableStateOf(PostDataModel())
    fun addPost() {
        fireStoreService.addPost(post = postModel.value.copy(geoPoint = GeoPoint(0.0, 0.0)))
    }
}