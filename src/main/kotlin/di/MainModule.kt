package com.example.di

import com.example.controller.user.UserController
import com.example.controller.user.UserControllerImpl
import com.example.util.Constants
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        val client = KMongo.createClient(
            connectionString = "mongodb+srv://connecto_user:qF0OENOhCBpXnJNN@cluster0.m0qcojn.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
        ).coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }

    single<UserController> {
        UserControllerImpl(get())
    }
}