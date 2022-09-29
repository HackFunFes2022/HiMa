package `fun`.hackathon.hima.data.services

import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.firestore.CollectionNames
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class FireStoreService @Inject constructor(val db:FirebaseFirestore):FireStoreInputScreenInterface,FireStoreMainScreenInterface,FireStoreDetailScreenInterface {
    override fun addPost(post:PostDataModel){
        if(post.title!=""&&post.geoPoint!=null){
            db.collection(CollectionNames.Posts.name).add(post).addOnSuccessListener { documentReference ->
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