package ru.smart.simplestatemachine.ui.view.delegates.base

import java.lang.IllegalArgumentException

class DelegateManager<T: Any, V: Any>(
    private val delegates: List<IDelegate<T, V>>
) {

    private var currentViewState: T? = null

    fun render(parent: V, viewState: T) {
        currentViewState?.let {
            findDelegate(it)?.apply {
                onUnBind()
                onVisibility(false)
            }
        }

        val delegate = findDelegate(viewState) ?: throw IllegalArgumentException("delegate not found for state = $viewState")

        delegate.apply {
            onCreateView(parent)
            onBind(viewState)
            onVisibility(true)
        }

        currentViewState = viewState
    }

    private fun findDelegate(viewState: T) = delegates.find { it.isFor(viewState) }
}