package `fun`.hackathon.hima.ui.viewmodels


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Preview
@Composable
fun InputScreen(viewModel: InputViewModel= viewModel()){
    val model= viewModel.postModel
    val isChecked = rememberSaveable{ mutableStateOf(false) }
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
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

            GoogleMap(
                modifier=Modifier.fillMaxWidth(0.9F).height(400.dp),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = singapore),
                    title = "Singapore",
                    snippet = "Marker in Singapore"
                )
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
            OutlinedButton(onClick = { /*TODO*/ }) {
                Text(text = "Ok")
            }
        }
    }
}