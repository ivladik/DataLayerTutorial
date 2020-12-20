package ru.cardsmobile.datalayertutorial.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import ru.cardsmobile.datalayertutorial.domain.usecase.GetRepositoriesUseCase
import ru.cardsmobile.datalayertutorial.domain.usecase.RefreshRepositoryUseCase
import ru.cardsmobile.datalayertutorial.presentation.mapper.GithubResultModelMapper
import ru.cardsmobile.datalayertutorial.presentation.model.GithubResultModel
import javax.inject.Inject

class GithubViewModel @Inject constructor(
    private val refreshRepositoryUseCase: RefreshRepositoryUseCase,
    private val getRepositoriesUseCase: GetRepositoriesUseCase,
    private val githubResultModelMapper: GithubResultModelMapper
) : ViewModel() {

    private val githubState = MutableLiveData<GithubResultModel>()
    private val compositeDisposable = CompositeDisposable()

    init {
        observeRepositories()
    }

    fun getGithubState(): LiveData<GithubResultModel> = githubState

    fun refreshData(userName: String) {
        compositeDisposable += refreshRepositoryUseCase(userName)
            .doOnSubscribe { githubState.postValue(GithubResultModel.Loading) }
            .map { result ->
                githubResultModelMapper.map(result)
            }
            .subscribe(
                {
                    Log.d(LOG_TAG, "Refresh completed - userName: $userName, result: $it")
                    githubState.postValue(it)
                },
                {
                    Log.d(LOG_TAG, "Refresh failed - userName: $userName, error: $it")
                    githubState.postValue(GithubResultModel.Error.Unexpected)
                }
            )
    }

    private fun observeRepositories() {
        compositeDisposable += getRepositoriesUseCase()
            .doOnSubscribe { githubState.postValue(GithubResultModel.Loading) }
            .map { result ->
                githubResultModelMapper.map(result)
            }
            .subscribe(
                {
                    Log.d(LOG_TAG, "Observe completed - result: $it")
                    githubState.postValue(it)
                },
                {
                    Log.d(LOG_TAG, "Observe failed - error: $it")
                    githubState.postValue(GithubResultModel.Error.Unexpected)
                }
            )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    private companion object {

        const val LOG_TAG = "GithubViewModel"
    }
}