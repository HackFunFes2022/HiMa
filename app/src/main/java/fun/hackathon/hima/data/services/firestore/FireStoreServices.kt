package `fun`.hackathon.hima.data.services

import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.firestore.CollectionNames
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FireStoreService @Inject constructor() : FireStoreInputScreenInterface,FireStoreLikeInterface {
    override fun addPost(post: PostDataModel, onSuccessListener: (it:DocumentReference)->Unit, onFailureListener: (it: Exception) -> Unit){
        if (post.title.isNotBlank()) {
            Firebase.firestore.collection(CollectionNames.Posts.tag).add(post).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener)
        }
        else{
            onFailureListener(Exception("You should write title"))
        }
    }

    override fun like(
        path: String,
        onSuccessListener: () -> Unit,
        onFailureListener: (it: Exception) -> Unit
    ) {
        Firebase.firestore.collection(CollectionNames.Posts.tag).document(path)
            .collection(CollectionNames.Likes.tag).document(Firebase.auth.currentUser!!.uid).set(
                mapOf<String, String>()
            ).addOnSuccessListener {
                onSuccessListener()
            }.addOnFailureListener(onFailureListener)
    }

    override fun unLike(
        path: String,
        onSuccessListener: () -> Unit,
        onFailureListener: (it: Exception) -> Unit
    ) {
        Firebase.firestore.collection(CollectionNames.Posts.tag).document(path)
            .collection(CollectionNames.Likes.tag).document(Firebase.auth.currentUser!!.uid)
            .delete().addOnSuccessListener {
                onSuccessListener()
            }.addOnFailureListener(onFailureListener)
    }

    override fun listenLikes(
        path: String,
        snapShotListener: (snapshot: QuerySnapshot?, e: FirebaseFirestoreException?) -> Unit
    ) {
        Firebase.firestore.collection(CollectionNames.Posts.tag).document(path)
            .collection(CollectionNames.Likes.tag).addSnapshotListener(snapShotListener)
    }

    fun getCollection(): CollectionReference {
        return Firebase.firestore.collection(CollectionNames.Posts.tag)
    }

    fun getPostDocument(id: String): DocumentReference {
        return Firebase.firestore.collection(CollectionNames.Posts.tag).document(id)
    }
}

interface FireStoreInputScreenInterface {
    fun addPost(post: PostDataModel,onSuccessListener: (it:DocumentReference)->Unit={},onFailureListener: (it: Exception) -> Unit={})
}

interface FireStoreMainScreenInterface:FireStoreLikeInterface{}

interface FireStoreDetailScreenInterface:FireStoreLikeInterface{}

interface  FireStoreLikeInterface{
    fun like(
        path: String,
        onSuccessListener: () -> Unit = {},
        onFailureListener: (it: Exception) -> Unit = {}
    )
    fun unLike(
        path: String,
        onSuccessListener: () -> Unit = {},
        onFailureListener: (it: Exception) -> Unit = {}
    )
    fun listenLikes(
        path: String,
        snapShotListener: (snapshot: QuerySnapshot?, e: FirebaseFirestoreException?) -> Unit
    )

}


@Module
@InstallIn(ViewModelComponent::class)
abstract class AnalyticsModule {
    @Binds
    abstract fun provideFireStore2Input(
        fireStoreService: FireStoreService
    ): FireStoreInputScreenInterface
    @Binds
    abstract fun provideFireStore2Detail(
        fireStoreService: FireStoreService
    ): FireStoreDetailScreenInterface
    @Binds
    abstract fun provideFireStore2Main(
        fireStoreService: FireStoreService
    ): FireStoreMainScreenInterface
}


@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}
