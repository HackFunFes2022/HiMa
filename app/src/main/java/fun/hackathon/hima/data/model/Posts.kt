package `fun`.hackathon.hima.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Posts(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val geoPoint: GeoPoint = GeoPoint(0.0, 0.0),
    val time: Timestamp = Timestamp.now()
) {
    companion object {
        fun fromPostDataModel(id: String, post: PostDataModel): Posts {
            return Posts(
                id,
                post.title,
                post.description,
                post.geoPoint,
                post.time
            )
        }
    }
}
