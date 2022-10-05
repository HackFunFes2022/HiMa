package `fun`.hackathon.hima.data.model

data class LikesDataModel(
    val ids: List<String> = listOf(),
) {
    fun count(): Int {
        return ids.count()
    }

    fun isContain(id: String): Boolean {
        return ids.contains(id)
    }
}