package ru.cardsmobile.datalayertutorial.data.provider

interface ConnectionInfoProvider {

    fun isConnectedToNetwork(): Boolean
}