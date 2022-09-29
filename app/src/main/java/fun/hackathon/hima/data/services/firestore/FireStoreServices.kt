package `fun`.hackathon.hima.data.services

import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.firestore.CollectionNames
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Inject

class FireStoreService @Inject constructor():FireStoreInputScreenInterface {
    override fun addPost(post:PostDataModel){
        if(post.title!=""&&post.geoPoint!=null){
            Firebase.firestore.collection(CollectionNames.Posts.tag).add(post).addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        }
    }

}

interface  FireStoreInputScreenInterface{
    fun addPost(post:PostDataModel)
}

interface  FireStoreMainScreenInterface{}

interface  FireStoreDetailScreenInterface{}


@Module
@InstallIn(ViewModelComponent::class)
abstract class AnalyticsModule {
    @Binds
    abstract fun provideFireStore(
        fireStoreService: FireStoreService
    ): FireStoreInputScreenInterface
}