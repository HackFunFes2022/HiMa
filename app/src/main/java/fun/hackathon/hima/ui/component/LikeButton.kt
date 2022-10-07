package `fun`.hackathon.hima.ui.component

import `fun`.hackathon.hima.data.model.LikesDataModel
import `fun`.hackathon.hima.data.services.FireStoreLikeInterface
import `fun`.hackathon.hima.data.services.FireStoreService
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

@Composable
fun LikeButton(
    modifier: Modifier = Modifier,
    fireStoreService: FireStoreLikeInterface = FireStoreService(),
    path: String,
    color: Color = Color.White
) {
    val colors = listOf(Color.Gray, Color.Yellow)
    val likes = remember {
        mutableStateOf(LikesDataModel())
    }

    fireStoreService.listenLikes(path, snapShotListener = { snapshot, e ->
        if (snapshot != null) {
            val docs = snapshot.documents
            val list = mutableListOf<String>()
            for (doc in docs) {
                list.add(doc.id)
            }
            likes.value = LikesDataModel(list)
        }
        if (e != null) Timber.d(e)
    })
    val flag = !likes.value.isContain(Firebase.auth.currentUser!!.uid)
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Text(
            if (likes.value.count() > 0) {
                "${likes.value.count()}"
            } else {
                ""
            }, color = color
        )

        IconButton(onClick = {
            if (flag) {
                fireStoreService.like(path)
            } else {
                fireStoreService.unLike(path)
            }
        }, content = {
            Icon(
                Icons.Default.Star, contentDescription = "", tint = if (flag) {
                    colors[0]
                } else {
                    colors[1]
                }
            )
        })
    }
}