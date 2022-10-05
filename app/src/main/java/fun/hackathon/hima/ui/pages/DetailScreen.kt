package `fun`.hackathon.hima.ui.pages

import `fun`.hackathon.hima.data.model.CommentDataModel
import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.firestore.CollectionNames
import `fun`.hackathon.hima.ui.component.LikeButton
import `fun`.hackathon.hima.ui.viewmodels.DetailViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DetailScreen(id: String,viewModel: DetailViewModel = hiltViewModel()) {
    val doc = viewModel.getDoc(id)
    val post = remember {
        mutableStateOf(PostDataModel())
    }
    val commentsCollection = doc.collection(CollectionNames.Comments.tag)
    val comments: MutableState<List<CommentDataModel>> = remember {
        mutableStateOf(listOf())
    }
    doc.get().addOnSuccessListener {
        post.value = PostDataModel.fromMap(it.data!!)
    }
    commentsCollection.addSnapshotListener { value, error ->
        val list = mutableListOf<CommentDataModel>()
        if (value != null) {
            for (snap in value.documents) {
                if (snap.data != null) {
                    list.add(CommentDataModel.fromMap(snap.data!!))
                }
            }
        }
        comments.value = list
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
                .paddingFromBaseline(top = 60.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "本文")
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp), contentAlignment = Alignment.Center
            ) {
                Text(text = post.value.description)
            }
            LikeRow(path = id)
            LazyColumn {
                items(comments.value) {
                    Text(text = it.comment)
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            CommentBox(onSubmit = {
                commentsCollection.add(CommentDataModel(comment = it))
            })
        }
    }
}

@Composable
fun CommentBox(onSubmit: (it: String) -> Unit) {
    val commentTextFieldState = remember {
        mutableStateOf("")
    }
    val isShowTextField = remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = commentTextFieldState.value,
            onValueChange = { commentTextFieldState.value = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "コメントを投稿する") }
        )
        when {
            commentTextFieldState.value != "" ->
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(onClick = {
                        isShowTextField.value = !isShowTextField.value
                        onSubmit(commentTextFieldState.value)
                        commentTextFieldState.value = ""
                    }) {
                        Text(text = "送信")
                    }
                }
        }
    }
}

@Composable
fun LikeRow(modifier: Modifier = Modifier, path: String) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "コメントを投稿する")
        LikeButton(path = path)
    }
}