package ru.cardsmobile.datalayertutorial.data.source

import io.reactivex.Single

interface NetworkSource<P, R> {

    fun performRequest(parameter: P): Single<R>
}