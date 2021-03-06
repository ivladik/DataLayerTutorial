package ru.cardsmobile.datalayertutorial.domain.exception

sealed class GithubException(message: String? = null, cause: Throwable? = null) :
    Throwable(message, cause) {

    class InvalidDataException(message: String? = null, cause: Throwable? = null) :
        GithubException(message, cause)

    class NoInternetException(message: String? = null, cause: Throwable? = null) :
        GithubException(message, cause)
}