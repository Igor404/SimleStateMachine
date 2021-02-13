package ru.smart.simplestatemachine.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import ru.smart.simplestatemachine.ui.statemachine.ViewState
import ru.smart.simplestatemachine.R
import ru.smart.simplestatemachine.ui.view.delegates.base.DelegateManager
import ru.smart.simplestatemachine.ui.viewmodel.ViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: ViewModel by viewModels()
    @Inject
    lateinit var delegateManager: DelegateManager<ViewState, AppCompatActivity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initObservers()
    }

    private fun initObservers() {
        viewModel.viewState.observe(this, Observer<ViewState>{
            delegateManager.render(this@MainActivity, it)
        })
        viewModel.loadingState.observe(this, Observer<Boolean>{
            progress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        viewModel.errorState.observe(this, Observer<ViewState.Error>{
            showAlert(it.text)
        })
    }

    private fun showAlert(text: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(text)
            show()
        }
    }

}