package `fun`.hackathon.hima.data.services

import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.firestore.CollectionNames
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Inject

class FireStoreService @Inject constructor() : FireStoreInputScreenInterface {
    override fun addPost(post: PostDataModel): Boolean {
        if (post.title != "" && post.geoPoint != null) {
            Firebase.firestore.collection(CollectionNames.Posts.tag)
            return true
        }
        return false
    }

    fun getCollection(): CollectionReference {
        return Firebase.firestore.collection(CollectionNames.Posts.tag)
    }
}

interface FireStoreInputScreenInterface {
    fun addPost(post: PostDataModel): Boolean
}

interface FireStoreMainScreenInterface {}

interface FireStoreDetailScreenInterface {}


@Module
@InstallIn(ViewModelComponent::class)
abstract class AnalyticsModule {
    @Binds
    abstract fun provideFireStore(
        fireStoreService: FireStoreService
    ): FireStoreInputScreenInterface
}