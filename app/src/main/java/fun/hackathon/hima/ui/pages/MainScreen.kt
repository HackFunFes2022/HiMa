package `fun`.hackathon.hima.ui.pages

import `fun`.hackathon.hima.LocalNavController
import `fun`.hackathon.hima.R
import `fun`.hackathon.hima.data.model.Params
import `fun`.hackathon.hima.data.model.PostDataModel
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
    val postList: MutableState<List<Map<String, Any>>> = remember {
        mutableStateOf(listOf())
    }
    val isShowDialog = remember {
        mutableStateOf(false)
    }
    viewModel.collection.addSnapshotListener { snapshot, e ->
        if (snapshot == null) {
            return@addSnapshotListener
        }
        val list = mutableListOf<Map<String, Any>>()
        for (dc in snapshot.documents) {
            val data = dc.data
            if (data != null) {
                data["id"] = dc.id
                list.add(data)
            }
        }
        postList.value = list
    }
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
        //MainContent()
        //PopUp(map = detailMap)
    }
}

@Composable
fun MainContent(latLng: LatLng, postList: List<Map<String, Any>>) {
    val cameraPosition = CameraPosition.fromLatLngZoom(latLng, 18f)
    val cameraPositionState = CameraPositionState(cameraPosition)
    val context = LocalContext.current
    val isShowPopUp = remember {
        mutableStateOf(false)
    }
    var detailMap = PostDataModel(title = "test").toMap().toMutableMap()
    detailMap["id"] = "1234"
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            for (postMap in postList) {
                val post = PostDataModel.fromMap(postMap)
                val id = postMap["id"]
                when {
                    post.geoPoint != null -> Marker(
                        title = post.title,
                        snippet = post.description,
                        state = MarkerState(
                            LatLng(
                                post.geoPoint.latitude,
                                post.geoPoint.longitude
                            )
                        ),
                        onInfoWindowClick = {
                            //navController.navigate(NavItem.DetailScreen.name+"/"+id)
                            //なぜかスタックする
                            detailMap = postMap.toMutableMap()
                            isShowPopUp.value = true
                        }
                    )
                }
            }
        }
        when {
            isShowPopUp.value -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                PopUp(map = detailMap)
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
fun PopUp(map: Map<String, Any?>) {
    val post = PostDataModel.fromMap(map)
    val id = map["id"] as String
    val navController = LocalNavController.current
    Column(
        Modifier
            .background(Color.White)
            .padding(bottom = 20.dp)
            .fillMaxWidth(0.8f)
            .height(100.dp)
    ) {
        Text(text = post.title, modifier = Modifier.padding(vertical = 10.dp), color = Color.Black)
        Text(
            text = post.description,
            modifier = Modifier.padding(vertical = 10.dp),
            color = Color.Black
        )
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                onClick = {
                    navController.navigate(NavItem.DetailScreen.name + "/" + id)
                }, content = { Text(text = "詳細表示") })//なぜか表示されない
        }
    }

}