package eho.jotangi.com

import org.koin.dsl.module

/**
 *Created by Luke Liu on 3/2/21.
 */
val viewModule = module {
    single {
        MainViewModel(get())
    }
}

val repoModule = module {
    single {
        MainRepository()
    }
}

val appModule = listOf(
    viewModule,
    repoModule
)