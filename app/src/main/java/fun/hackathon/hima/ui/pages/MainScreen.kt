package `fun`.hackathon.hima.ui.pages

import `fun`.hackathon.hima.LocalNavController
import `fun`.hackathon.hima.R
import `fun`.hackathon.hima.data.model.Params
import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.model.Posts
import `fun`.hackathon.hima.ui.viewmodels.MainViewModel
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val context = LocalContext.current

    val postList: MutableState<List<Map<String, Any>>> = remember {
        mutableStateOf(listOf())
    }
    val uiState by remember { mutableStateOf(viewModel.mainUiState) }
    viewModel.startFetch(context = context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.app_name))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(NavItem.InputScreen.name)
                }
            ) {
                Icon(Icons.Filled.Add, "Input")
            }
        }
    ) {
        var detailMap = PostDataModel(title = "test").toMap().toMutableMap()
        detailMap["id"] = "1234"
        MainContent(LatLng(0.0, 0.0), uiState.postData)
        //PopUp(map = detailMap)
    }
}

@Composable
fun MainContent(latLng: LatLng, postsList: List<Posts>) {
    val cameraPosition = CameraPosition.fromLatLngZoom(latLng, 18f)
    val cameraPositionState = CameraPositionState(cameraPosition)
    val context = LocalContext.current

    val isShowPopUp = remember { mutableStateOf(false) }
    val detailPosts = remember { mutableStateOf(Posts(id = "1234")) }

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            for (posts in postsList) {
                Marker(
                    title = posts.title,
                    snippet = posts.description,
                    state = MarkerState(
                        LatLng(
                            posts.geoPoint.latitude,
                            posts.geoPoint.longitude
                        )
                    ),
                    onInfoWindowClick = {
                        //navController.navigate(NavItem.DetailScreen.name+"/"+id)
                        //なぜかスタックする
                        detailPosts.value = posts
                        isShowPopUp.value = true
                    }
                )
            }
        }
        if (isShowPopUp.value) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                PopUp(detailPosts.value)
            }
        }
    } else {
        // 許可ダイアログ出しといた方が良さそう
        // 許可求める
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            Params.REQUEST_CODE_LOCATION
        )
    }
}

@Composable
fun PopUp(posts: Posts) {
    val navController = LocalNavController.current

    Column(
        Modifier
            .background(Color.White)
            .padding(bottom = 20.dp)
            .fillMaxWidth(0.8f)
            .height(100.dp)
    ) {
        Text(text = posts.title, modifier = Modifier.padding(vertical = 10.dp), color = Color.Black)
        Text(
            text = posts.description,
            modifier = Modifier.padding(vertical = 10.dp),
            color = Color.Black
        )
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                onClick = {
                    navController.navigate(NavItem.DetailScreen.name + "/" + posts.id)
                }, content = { Text(text = "詳細表示") })//なぜか表示されない
        }
    }

}