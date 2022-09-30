package `fun`.hackathon.hima.ui.pages

import `fun`.hackathon.hima.Greeting
import `fun`.hackathon.hima.LocalNavController
import `fun`.hackathon.hima.R
import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.ui.viewmodels.MainViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MainScreen(viewModel:MainViewModel= hiltViewModel()) {
    val navController = LocalNavController.current
    val postList: MutableState<List<PostDataModel>>
            =remember{
        mutableStateOf(listOf())
    }
    val Hakodate=LatLng( 41.7687,140.7288)
    val cameraPosition=CameraPosition.fromLatLngZoom(Hakodate,10f)
    val cameraPositionState= CameraPositionState(cameraPosition)
    viewModel.collection.addSnapshotListener(){ snapshot,e->
        if(snapshot==null){
            return@addSnapshotListener
        }
        val list= mutableListOf<PostDataModel>()
        for (dc in snapshot!!.documents){
            val data=dc.data
            if (data != null) {
                val post= PostDataModel.fromMap(data)
                list.add(post)
            }
        }
        postList.value=list
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
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState =cameraPositionState
        ){
            for (post in postList.value){
                when{
                    post.geoPoint!=null->Marker(
                        state = MarkerState(LatLng(post.geoPoint!!.latitude,post.geoPoint!!.longitude))
                    )
                }
            }
        }
    }
}