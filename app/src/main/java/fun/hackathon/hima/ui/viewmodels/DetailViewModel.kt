package `fun`.hackathon.hima.ui.viewmodels

import `fun`.hackathon.hima.data.model.CommentDataModel
import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.model.Posts
import `fun`.hackathon.hima.data.services.FireStoreService
import `fun`.hackathon.hima.data.services.firestore.CollectionNames
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val fireStoreService: FireStoreService,
) : ViewModel() {
    private var _documentState = MutableStateFlow(DocumentState())
    val documentState get() = _documentState.asStateFlow()

    private var document: DocumentReference? = null
    private var commentsCollection: CollectionReference? = null

    fun getDocument(id:String) {
        _documentState.value = _documentState.value.copy(loading = true)

        try {
            document = fireStoreService.getPostDocument(id)
            document!!.get().addOnSuccessListener {
                _documentState.value =
                    _documentState.value.copy(posts = PostDataModel.fromMap(it.data!!))
            }

            commentsCollection = document!!.collection(CollectionNames.Comments.tag)

            commentsCollection!!.addSnapshotListener { value, error ->
                if (error != null) {
                    _documentState.value = _documentState.value.copy(loading = false, error = error)
                    return@addSnapshotListener
                }

                val list = mutableListOf<CommentDataModel>()
                if (value != null) {
                    for (snap in value.documents) {
                        if (snap.data != null) {
                            list.add(CommentDataModel.fromMap(snap.data!!))
                        }
                    }
                }
                _documentState.value = _documentState.value.copy(comments = list)
            }
        } catch (e: Exception) {
            _documentState.value = _documentState.value.copy(loading = false, error = e)
            return
        }

        _documentState.value = _documentState.value.copy(loading = false)
    }

    fun onCommentSubmit(comment: String) {
        commentsCollection?.add(CommentDataModel(comment = comment))
    }
}

data class DocumentState(
    val loading: Boolean = false,
    val posts: PostDataModel = PostDataModel(),
    val comments: List<CommentDataModel> = emptyList(),
    val error: Exception? = null
)