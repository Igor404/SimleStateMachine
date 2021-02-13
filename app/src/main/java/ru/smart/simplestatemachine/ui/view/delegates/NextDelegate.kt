package ru.smart.simplestatemachine.ui.view.delegates

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.smart.simplestatemachine.ui.statemachine.ViewState
import ru.smart.simplestatemachine.R
import ru.smart.simplestatemachine.ui.view.delegates.base.IDelegate

class NextDelegate: IDelegate<ViewState, AppCompatActivity> {

    private lateinit var stateName: TextView
    private lateinit var btnNext: Button

    override fun onBind(viewState: ViewState) {
        viewState as ViewState.NextViewState

        stateName.text = viewState.label
        btnNext.text = viewState.label
        btnNext.setOnClickListener {
            viewState.nextCallback.invoke()
        }
    }

    override fun onUnBind() {
        btnNext.setOnClickListener(null)
    }

    override fun isFor(entry: ViewState) = entry is ViewState.NextViewState

    override fun onVisibility(isVisible: Boolean) {
        val v = if (isVisible) View.VISIBLE else View.INVISIBLE
        stateName.visibility = v
        btnNext.visibility = v
    }

    override fun onCreateView(parent: AppCompatActivity) {
        stateName = parent.findViewById(R.id.stateName)
        btnNext = parent.findViewById(R.id.btnNext)
    }

}