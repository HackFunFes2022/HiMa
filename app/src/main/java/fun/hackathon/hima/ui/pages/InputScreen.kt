package `fun`.hackathon.hima.ui.pages


import `fun`.hackathon.hima.LocalNavController
import `fun`.hackathon.hima.R
import `fun`.hackathon.hima.ui.viewmodels.InputViewModel
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState


@Preview
@Composable
fun InputScreen(viewModel: InputViewModel = hiltViewModel()) {
    val model = viewModel.postModel
    val isChecked = rememberSaveable { mutableStateOf(true) }
    val context = LocalContext.current
    val navController = LocalNavController.current
    val takePicture by viewModel.imageState.collectAsState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        if (it != null) viewModel.setImage(it)
    }

    viewModel.fetchLocation(context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "ヒヤリ体験を入力") }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                OutlinedTextField(
                    modifier = Modifier.padding(20.dp),
                    value = model.value.title,
                    onValueChange = { model.value = model.value.copy(title = it) },
                    //titleの更新
                    label = { Text(text = "タイトルは？") })
                OutlinedTextField(
                    modifier = Modifier.padding(20.dp),
                    value = model.value.description,
                    onValueChange = { model.value = model.value.copy(description = it) },
                    //descriptionの更新
                    label = { Text(text = "どんなことにヒヤリとした？") })
                //Button(onClick = {}, content = { Text(text = "地図を開く")})
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth(0.9F)
                        .height(400.dp),

                    cameraPositionState = viewModel.positionState.value,
                    onMapClick = {
                        if (!isChecked.value) {
                            //現在地を利用しない場合
                            //ロングタップでマーカーを移動
                            viewModel.updateGeoPoint(it)
                        }
                    }
                ) {
                    Marker(
                        state = MarkerState(
                            LatLng(
                                viewModel.postModel.value.geoPoint.latitude,
                                viewModel.postModel.value.geoPoint.longitude
                            )
                        )
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "現在地で登録する")
                    Checkbox(
                        checked = isChecked.value,
                        onCheckedChange = {
                            //チェックボックス
                            isChecked.value = it
                            if (it) {
                                viewModel.fetchLocation(context)//現在地にリセット
                            }
                        }
                    )
                }
                TakePicture(takePicture.data) {
                    launcher.launch()
                }
                OutlinedButton(onClick = {
                    val exception = viewModel.addPost()
                if (exception==null) {
                    //とりあえず仮置き
                    navController.navigate(NavItem.MainScreen.name)
                    }
                }) {
                    Text(text = "Ok")
                }
            }
        }
    }
}

@Composable
fun TakePicture(
    data: Bitmap?,
    onClick: () -> Unit = {}
) {
    if (data != null) {
        Box(
            modifier = Modifier.fillMaxWidth(0.9f).height(200.dp)
        ) {
            Image(
                data.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier.fillMaxSize().clickable {
                    onClick()
                }
            )
        }
        Button(
            onClick = onClick
        ) {
            Text(stringResource(id = R.string.input_take_picture_retry_button))
        }
    } else {
        Button(
            onClick = onClick
        ) {
            Text(stringResource(id = R.string.input_take_picture_button))
        }
    }
}