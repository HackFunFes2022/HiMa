package `fun`.hackathon.hima.ui.viewmodels

import `fun`.hackathon.hima.data.services.FireStoreService
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fireStoreService: FireStoreService,
) : ViewModel() {
    val collection = fireStoreService.getCollection()
}