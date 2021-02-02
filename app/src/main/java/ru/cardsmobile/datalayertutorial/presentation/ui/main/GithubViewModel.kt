package ru.cardsmobile.datalayertutorial.presentation.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import ru.cardsmobile.datalayertutorial.domain.usecase.GetGithubResultUseCase
import ru.cardsmobile.datalayertutorial.domain.usecase.RefreshGithubResultUseCase
import ru.cardsmobile.datalayertutorial.presentation.model.GithubState
import javax.inject.Inject

class GithubViewModel @Inject constructor(
    private val refreshGithubResultUseCase: RefreshGithubResultUseCase,
    private val getGithubResultUseCase: GetGithubResultUseCase
) : ViewModel() {

    private val githubState = MutableLiveData<GithubState>()
    private val compositeDisposable = CompositeDisposable()

    init {
        observeGithubResult()
    }

    fun getGithubState(): LiveData<GithubState> = githubState

    fun refreshGithubResult(userName: String) {
        compositeDisposable += refreshGithubResultUseCase(userName)
            .doOnSubscribe { githubState.postValue(GithubState.Loading) }
            .subscribeBy(
                onSuccess = {
                    Log.d(LOG_TAG, "Refresh completed - userName: $userName, result: $it")
                    githubState.postValue(GithubState.Success(it))
                },
                onError = {
                    Log.d(LOG_TAG, "Refresh failed - userName: $userName, error: $it")
                    githubState.postValue(GithubState.Error(it))
                }
            )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    private fun observeGithubResult() {
        compositeDisposable += getGithubResultUseCase()
            .doOnSubscribe { githubState.postValue(GithubState.Loading) }
            .subscribeBy(
                onNext = {
                    Log.d(GithubViewModel.LOG_TAG, "Observe completed - result: $it")
                    githubState.postValue(GithubState.Success(it))
                },
                onError = {
                    Log.d(GithubViewModel.LOG_TAG, "Observe failed - error: $it")
                    githubState.postValue(GithubState.Error(it))
                }
            )
    }

    private companion object {

        const val LOG_TAG = "GithubViewModel"
    }
}