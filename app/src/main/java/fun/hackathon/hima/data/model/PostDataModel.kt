package `fun`.hackathon.hima.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint


data class PostDataModel(
    val title: String = "",
    val description: String = "",
    val geoPoint: GeoPoint? = null,
    val time: Timestamp = Timestamp.now()
) {
    companion object {
        fun fromMap(map: Map<String, Any?>): PostDataModel {
            val data = PostDataModel(
                title = map["title"] as String,
                description = map["description"] as String,
                geoPoint = map["geoPoint"] as GeoPoint?,
                time = map["time"] as Timestamp
            )
            println(data)
            return data
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