package `fun`.hackathon.hima.ui.input

import `fun`.hackathon.hima.data.model.PostDataModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class InputViewModel:ViewModel() {
    val postModel = mutableStateOf(PostDataModel())
}