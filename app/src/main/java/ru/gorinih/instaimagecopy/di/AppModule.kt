package ru.gorinih.instaimagecopy.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.gorinih.instaimagecopy.MainViewModel

/**
 * Created by Igor Abdulganeev on 20.10.2026
 */

val appModule = module {

    viewModel<MainViewModel> { MainViewModel(webLoader = get()) }
}