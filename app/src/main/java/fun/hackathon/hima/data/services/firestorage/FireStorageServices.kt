package `fun`.hackathon.hima.data.services.firestorage

import `fun`.hackathon.hima.data.services.FireStoreService
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import java.util.UUID
import javax.inject.Inject

class FireStorageService @Inject constructor() :FireStorageServiceInterface{
    private val instance= Firebase.storage
    private val ref=instance.reference
    override fun uploadImage(bytes:ByteArray, onCompleteListener:(it: Task<UploadTask.TaskSnapshot>)->Unit, onFailureListener:(e:Exception)->Unit){
        val mountedRef=ref.child(UUID.randomUUID().toString()+".jpg")//こいつ、pngの可能性を考慮していない！？正気か......
        val task =mountedRef.putBytes(bytes)
        task.addOnCompleteListener(onCompleteListener).addOnFailureListener(onFailureListener)
    }
}

interface FireStorageServiceInterface{
    fun uploadImage(bytes:ByteArray,onCompleteListener:(it: Task<UploadTask.TaskSnapshot>)->Unit={},onFailureListener:(e:Exception)->Unit={})
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class FireStorageModule {
    @Binds
    abstract fun provideFireStorage(
        fireStorageService: FireStorageService
    ): FireStorageServiceInterface
}