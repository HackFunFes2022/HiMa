package `fun`.hackathon.hima.ui.pages

import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.firestore.CollectionNames
import `fun`.hackathon.hima.ui.viewmodels.DetailViewModel
import `fun`.hackathon.hima.ui.viewmodels.InputViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DetailScreen(id: String,viewModel: DetailViewModel = hiltViewModel()) {
    val doc=viewModel.getDoc(id)
    val commentCol=doc.collection("comments")
    val post= remember {
        mutableStateOf(PostDataModel())
    }
    val comment= remember {
        mutableStateOf("")
    }
    doc.get().addOnSuccessListener {
        post.value=PostDataModel.fromMap(it.data!!)
    }
    doc.collection(CollectionNames.Comments.tag).get().addOnSuccessListener {
        for (val snap in it.documents){
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = post.value.title) }
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .paddingFromBaseline(top = 20.dp) ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                Text(text = "本文")
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                Text(text = post.value.description)
            }
            Row() {
                Text(text = "コメント")
            }
            LazyColumn{}
        }
    }
}