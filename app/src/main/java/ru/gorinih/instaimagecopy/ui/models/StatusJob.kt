package ru.gorinih.instaimagecopy.ui.models

/**
 * Created by Igor Abdulganeev on 28.10.2026
 */

enum class StatusJob{
    WAITING_JOB, //приложение готово для разбора поста и загрузки данных
    DOWNLOAD_POST, //загрузка поста
    DOWNLOAD_MEDIA, //загрузка данных
    RESEARCH, //поиск данных
}