package `fun`.hackathon.hima.ui.pages


import `fun`.hackathon.hima.ui.viewmodels.InputViewModel
import android.location.LocationManager
import android.location.LocationRequest
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Preview
@Composable
fun InputScreen(viewModel: InputViewModel = hiltViewModel()){
    val model= viewModel.postModel
    val isChecked = rememberSaveable{ mutableStateOf(false) }
    viewModel.fetchLocation(LocalContext.current)
    val currentLatLng=viewModel.latLngState.value
    val cameraPositionState = viewModel.positionState
    val inputLatLng:MutableState<LatLng?> = rememberSaveable() {
        mutableStateOf(null)
    }

    Scaffold(
        topBar = { TopAppBar(
            title = {Text(text = "ヒヤリ体験を入力")}
        )}
    ) {
        Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(modifier = Modifier.padding(20.dp),value = model.value.title, onValueChange = {model.value=model.value.copy(title = it)}, label = { Text(text = "タイトルは？")})
            OutlinedTextField(modifier = Modifier.padding(20.dp),value = model.value.description, onValueChange = {model.value=model.value.copy(description = it)}, label = { Text(text = "どんなことにヒヤリとした？")})
            //Button(onClick = {}, content = { Text(text = "地図を開く")})
            //まだ使わないのでコメントアウトします！！！節約のため
            GoogleMap(
                modifier= Modifier
                    .fillMaxWidth(0.9F)
                    .height(400.dp),
                cameraPositionState = cameraPositionState,
                onMapLongClick = {
                    inputLatLng.value=it
                }
            ) {
                Marker(
                    state = MarkerState(position = currentLatLng),
                    title = "あなたの位置"
                )
                when{
                    inputLatLng.value!=null -> Marker(
                        state = MarkerState(position = inputLatLng.value!!)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "現在地で登録する")
                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = {
                        isChecked.value = it
                    }
                )
            }
            OutlinedButton(onClick = {
                if(isChecked.value){
                    viewModel.postModel.value=viewModel.postModel.value.copy(geoPoint = GeoPoint(currentLatLng.latitude,currentLatLng.longitude))
                }
                else if(inputLatLng.value!=null){
                    viewModel.postModel.value=viewModel.postModel.value.copy(geoPoint = GeoPoint(
                        inputLatLng.value!!.latitude,inputLatLng.value!!.longitude))
                }
                else{
                    return@OutlinedButton
                }
                viewModel.addPost()
            }) {
                Text(text = "Ok")
            }
        }
    }
}