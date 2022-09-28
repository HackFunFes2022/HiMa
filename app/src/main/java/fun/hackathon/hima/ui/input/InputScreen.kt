package `fun`.hackathon.hima.ui.input

import `fun`.hackathon.hima.data.model.PostDataModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
@Preview
@Composable
fun InputScreen(viewModel: InputViewModel= viewModel()){
    val model= viewModel.postModel
    Scaffold(
        topBar = { TopAppBar(
            title = {Text(text = "ヒヤリ体験を入力")}
        )}
    ) {
        Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(modifier = Modifier.padding(20.dp),value = model.value.title, onValueChange = {model.value=model.value.copy(title = it)}, label = { Text(text = "タイトルは？")})
            OutlinedTextField(modifier = Modifier.padding(20.dp),value = model.value.description, onValueChange = {model.value=model.value.copy(description = it)}, label = { Text(text = "どんなことにヒヤリとした？")})
        }
    }
}