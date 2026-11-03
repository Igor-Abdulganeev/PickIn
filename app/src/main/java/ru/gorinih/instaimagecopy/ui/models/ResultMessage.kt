package ru.gorinih.instaimagecopy.ui.models

/**
 * Created by Igor Abdulganeev on 02.11.2026
 */

data class ResultMessage(
    val message: Int,
    val type: TypeResult,
    val params: Array<String> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResultMessage

        if (message != other.message) return false
        if (type != other.type) return false
        if (!params.contentEquals(other.params)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message
        result = 31 * result + type.hashCode()
        result = 31 * result + params.contentHashCode()
        return result
    }
}
