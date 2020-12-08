package ru.cardsmobile.datalayertutorial.presentation.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import ru.cardsmobile.datalayertutorial.R
import ru.cardsmobile.datalayertutorial.di.AppInjector
import ru.cardsmobile.datalayertutorial.presentation.model.GithubResultModel
import ru.cardsmobile.datalayertutorial.presentation.model.RepositoryModel
import ru.cardsmobile.datalayertutorial.presentation.viewmodel.GithubViewModel
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
            viewModel.refreshData(et_username.text.toString())
        }

        observeGithubViewModel()
    }

    private fun observeGithubViewModel() {
        viewModel.getGithubState().observe(this, Observer { response ->
            when (response) {
                is GithubResultModel.Success -> showData(response.userName, response.repository)
                GithubResultModel.Empty -> showEmptyData()
                GithubResultModel.Loading -> showLoading()
                GithubResultModel.Error.RepeatedRequest -> showRepeatedRequestError()
                GithubResultModel.Error.NoInternetException -> showNoInternetError()
                GithubResultModel.Error.Unexpected -> showOtherError()
            }
        })
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
        repositoryModelList: List<RepositoryModel>
    ) {
        et_username.setText(userName)
        tv_result.text = getString(R.string.repo_amount_result_text, repositoryModelList.size)
    }
}