package `fun`.hackathon.hima.ui.pages

import `fun`.hackathon.hima.LocalNavController
import `fun`.hackathon.hima.R
import `fun`.hackathon.hima.data.model.CommentDataModel
import `fun`.hackathon.hima.data.model.PostDataModel
import `fun`.hackathon.hima.data.services.firestore.CollectionNames
import `fun`.hackathon.hima.ui.component.LikeButton
import `fun`.hackathon.hima.ui.component.ListSpacer
import `fun`.hackathon.hima.ui.component.LoadingCircle
import `fun`.hackathon.hima.ui.viewmodels.DetailViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DetailScreen(id: String, viewModel: DetailViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    val documentState by viewModel.documentState.collectAsState()
    viewModel.getDocument(id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.detail_top_bar_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        when {
            documentState.loading -> {
                LoadingCircle()
            }
            documentState.error != null -> {

            }
            else -> {
                DetailContent(
                    id,
                    documentState.posts,
                    documentState.comments
                ) { comment ->
                    viewModel.onCommentSubmit(comment)
                }
            }
        }
    }
}

@Composable
fun DetailContent(
    id: String = "",
    posts: PostDataModel,
    comments: List<CommentDataModel>,
    onSubmit: (comment: String) -> Unit = {}
) {
    Column(
        Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = posts.title,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = posts.description,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            style = MaterialTheme.typography.body1
        )
        CommentRow(likePath = id)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            items(comments) {
                Text(text = it.comment)
                ListSpacer()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        CommentLine(onSubmit = onSubmit)
    }
}

@Composable
fun CommentLine(onSubmit: (comment: String) -> Unit) {
    val commentTextFieldState = remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(8f),
            value = commentTextFieldState.value,
            onValueChange = { commentTextFieldState.value = it },
            label = {
                Text(text = stringResource(id = R.string.detail_comment_input_field_label))
            }
        )
        Button(
            modifier = Modifier.weight(2f),
            enabled = commentTextFieldState.value.isNotBlank(),
            onClick = {
                onSubmit(commentTextFieldState.value)
                commentTextFieldState.value = ""
            }
        ) {
            Text(text = stringResource(id = R.string.detail_comment_send_button))
        }
    }
}

@Composable
fun CommentRow(modifier: Modifier = Modifier, likePath: String) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "コメント",
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        LikeButton(path = likePath)
    }
}