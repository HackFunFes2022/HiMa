package `fun`.hackathon.hima.ui.viewmodels

import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.FireStoreService
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val fireStoreService: FireStoreService,
) : ViewModel() {
    fun getDoc(id:String): DocumentReference {
        val doc=fireStoreService.getPostDocument(id)
        return doc
    }
}