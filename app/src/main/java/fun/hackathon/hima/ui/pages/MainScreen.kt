package `fun`.hackathon.hima.ui.pages

import `fun`.hackathon.hima.LocalNavController
import `fun`.hackathon.hima.R
import `fun`.hackathon.hima.data.model.Params
import `fun`.hackathon.hima.data.model.Posts
import `fun`.hackathon.hima.ui.component.LoadingCircle
import `fun`.hackathon.hima.ui.viewmodels.MainViewModel
import `fun`.hackathon.hima.util.toLatLng
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import timber.log.Timber

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val context = LocalContext.current

    val uiState by viewModel.mainUiState.collectAsState()
    val locationState by viewModel.nowLocationState.collectAsState()
    viewModel.startFetch(context = context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.app_name))
                },
                actions = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_icon),
                        contentDescription = ""
                    )
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
        when {
            uiState.error != null -> {
                val e = uiState.error
                val openDialog = remember { mutableStateOf(true) }
                Timber.e(e)

                if (openDialog.value) {
                    AlertDialog(
                        onDismissRequest = { /* 空白にすると画面外タップでダイアログが消える */ },
                        title = { Text("${e!!.cause}") },
                        text = { Text("${e!!.message}") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    openDialog.value = false
                                }
                            ) {
                                Text(stringResource(id = R.string.dialog_ok))
                            }
                        }
                    )
                }
            }
            uiState.loading -> {
                LoadingCircle()
            }
            else -> {
                val cameraPosition = CameraPosition.fromLatLngZoom(locationState.location.toLatLng(), 18f)
                val cameraPositionState = CameraPositionState(cameraPosition)
                Timber.d("${uiState.postData}")
                MainContent(cameraPositionState, uiState.postData)
            }
        }
    }
}

@Composable
fun MainContent(
    cameraPositionState: CameraPositionState,
    postsList: List<Posts>,
) {
    val context = LocalContext.current

    val isShowPopUp = remember { mutableStateOf(false) }
    val detailPosts = remember { mutableStateOf(Posts()) }

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
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
                    onClick = {
                        detailPosts.value = posts
                        isShowPopUp.value = true
                        true
                    },
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
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                onClick = {
                    navController.navigate(NavItem.DetailScreen.name + "/" + posts.id)
                },
                content = { Text(text = "詳細表示") }
            )//なぜか表示されない
        }
    }
}