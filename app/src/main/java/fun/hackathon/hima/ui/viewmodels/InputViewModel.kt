package `fun`.hackathon.hima.ui.viewmodels

import `fun`.hackathon.hima.data.model.PostDataModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class InputViewModel:ViewModel() {
    val postModel = mutableStateOf(PostDataModel())
}