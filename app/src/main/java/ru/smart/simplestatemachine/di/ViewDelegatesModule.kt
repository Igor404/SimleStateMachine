package ru.smart.simplestatemachine.di

import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import ru.smart.simplestatemachine.ui.statemachine.ViewState
import ru.smart.simplestatemachine.ui.view.delegates.InitDelegate
import ru.smart.simplestatemachine.ui.view.delegates.NextDelegate
import ru.smart.simplestatemachine.ui.view.delegates.base.DelegateManager

@InstallIn(ActivityComponent::class)
@Module
class ViewDelegatesModule {

    @Provides
    fun provideDelegateManager(): DelegateManager<ViewState, AppCompatActivity> {
        return DelegateManager(
            listOf(InitDelegate(), NextDelegate())
        )
    }

}