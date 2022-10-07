package `fun`.hackathon.hima.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint


data class PostDataModel(
    val title: String = "",
    val description: String = "",
    val geoPoint: GeoPoint = GeoPoint(0.0, 0.0),
    val time: Timestamp = Timestamp.now(),
    val imageUrl: String? = null,
) {
    companion object {
        fun fromMap(map: Map<String, Any?>): PostDataModel {
            return PostDataModel(
                title = map["title"] as String,
                description = map["description"] as String,
                geoPoint = (map["geoPoint"] ?: GeoPoint(0.0, 0.0)) as GeoPoint,
                time = map["time"] as Timestamp
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return mapOf<String, Any?>(
            "title" to title,
            "description" to description,
            "time" to time,
            "geoPoint" to geoPoint,
        )
    }
}