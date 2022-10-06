package `fun`.hackathon.hima.util

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

fun Location.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

fun GeoPoint.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}