package ru.gorinih.instaimagecopy.ui.models

/**
 * Created by Igor Abdulganeev on 28.10.2026
 */

enum class TypeDownload(val index: Int) {
    AUTO(index = 0),
    MANUAL(index = 1),
    NONE(index = 100)
}