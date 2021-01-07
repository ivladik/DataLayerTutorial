package ru.cardsmobile.datalayertutorial.presentation.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import ru.cardsmobile.datalayertutorial.R
import ru.cardsmobile.datalayertutorial.di.AppInjector
import ru.cardsmobile.datalayertutorial.domain.entity.Repository
import ru.cardsmobile.datalayertutorial.domain.exception.GithubException.EmptyDataException
import ru.cardsmobile.datalayertutorial.domain.exception.GithubException.InvalidDataException
import ru.cardsmobile.datalayertutorial.domain.exception.GithubException.NoInternetException
import ru.cardsmobile.datalayertutorial.domain.exception.GithubException.RepeatedRequestException
import ru.cardsmobile.datalayertutorial.presentation.model.GithubState
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: GithubViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GithubViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppInjector.component.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_refresh.setOnClickListener {
            viewModel.refreshGithubResult(et_username.text.toString())
        }

        observeGithubViewModel()
    }

    private fun observeGithubViewModel() {
        viewModel.getGithubState().observe(this, Observer { state ->
            when (state) {
                is GithubState.Success -> showData(
                    state.githubResult.userName,
                    state.githubResult.repositories
                )
                GithubState.Loading -> showLoading()
                is GithubState.Error -> handleError(state.throwable)
            }
        })
    }

    private fun handleError(throwable: Throwable) {
        when (throwable) {
            is EmptyDataException -> showEmptyData()
            is RepeatedRequestException -> showRepeatedRequestError()
            is NoInternetException -> showNoInternetError()
            is InvalidDataException -> showOtherError()
            else -> showOtherError()
        }
    }

    private fun showNoInternetError() {
        tv_result.text = getString(R.string.error_text)
        Toast
            .makeText(this, R.string.no_internet_error_text, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showEmptyData() {
        et_username.setText(R.string.enter_username_text)
        tv_result.text = getString(R.string.result_text)
    }

    private fun showRepeatedRequestError() {
        Toast
            .makeText(this, R.string.repeated_error_text, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showLoading() {
        tv_result.text = getString(R.string.loading_text)
    }

    private fun showOtherError() {
        tv_result.text = getString(R.string.error_text)
        Toast
            .makeText(this, R.string.other_error_text, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showData(
        userName: String,
        repositoryModelList: List<Repository>
    ) {
        et_username.setText(userName)
        tv_result.text = getString(R.string.repo_amount_result_text, repositoryModelList.size)
    }
}