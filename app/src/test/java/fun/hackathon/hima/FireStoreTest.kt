package `fun`.hackathon.hima

import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.FireStoreService
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import org.junit.Assert
import org.junit.Test

class FireStoreTest {
    @Test
    fun addition_isCorrect() {
        val firestoreHost = "localhost"
        val firestorePort = 8080
        val fire=FireStoreService(Firebase.firestore.apply { this.useEmulator(firestoreHost, firestorePort)
            this.firestoreSettings = firestoreSettings {
                isPersistenceEnabled = false
            } })
        fire.addPost(PostDataModel(title = "test", description = "description here", time = Timestamp.now(), geoPoint = GeoPoint(10.0, 10.0)))
    }
}