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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.launch
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
                val cameraPosition =
                    CameraPosition.fromLatLngZoom(locationState.location.toLatLng(), 18f)
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
    val scope = rememberCoroutineScope()

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
            properties = MapProperties(isMyLocationEnabled = true),
            // FABに被ったので、一旦ZoomControlsを消してる
            // TODO: ZoomControlsをどうするか検討
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            onMapClick = {
                isShowPopUp.value = false
            }
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
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(posts.geoPoint.toLatLng(), 18f, 0f, 0f)
                                ),
                                durationMs = 1000
                            )
                        }

                        detailPosts.value = posts
                        isShowPopUp.value = true
                        true
                    }
                )
            }
        }
        PostsPopUp(isShowPopUp.value, detailPosts.value)
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
fun PostsPopUp(isVisible: Boolean, posts: Posts) {
    val navController = LocalNavController.current
    val popupHeight = 220.dp

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideIn(tween(100, easing = LinearOutSlowInEasing)) { fullSize ->
                IntOffset(0, fullSize.height - popupHeight.value.toInt())
            },
            exit = slideOut(tween(100, easing = FastOutSlowInEasing)) { fullSize ->
                IntOffset(0, fullSize.height)
            }
        ) {
            Box(
                modifier = Modifier
                    .height(popupHeight)
                    .fillMaxWidth(0.8f)
            ) {
                Column(
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colors.primary,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(10)
                        )
                        .padding(start = 8.dp, end = 8.dp)
                ) {
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = posts.title,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.body1,

                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = posts.description,
                        color = Color.Black,
                        style = MaterialTheme.typography.body2
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                navController.navigate(NavItem.DetailScreen.name + "/" + posts.id)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.main_popup_detail_button))
                        }
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}