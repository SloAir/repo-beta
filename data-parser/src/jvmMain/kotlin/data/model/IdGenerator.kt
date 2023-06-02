package data.model

object IdGenerator {
    var id: Int = 0

    fun setId(): Int { return id++ }
}