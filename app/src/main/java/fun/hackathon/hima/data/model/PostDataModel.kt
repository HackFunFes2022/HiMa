package `fun`.hackathon.hima.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint


data class PostDataModel(val title:String="",val description:String="",val geoPoint: GeoPoint?=null,val time:Timestamp= Timestamp.now())