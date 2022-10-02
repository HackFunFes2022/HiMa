package `fun`.hackathon.hima.data.model

data class CommentDataModel(
    val comment: String
) {
    companion object {
        fun fromMap(map: Map<String, Any>): CommentDataModel {
            val data = CommentDataModel(comment = map["comment"] as String)
            return data
        }
    }
}