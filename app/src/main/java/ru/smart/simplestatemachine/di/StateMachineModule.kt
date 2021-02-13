package ru.smart.simplestatemachine.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.smart.simplestatemachine.ui.statemachine.Event
import ru.smart.simplestatemachine.ui.statemachine.State
import ru.smart.simplestatemachine.ui.statemachine.base.StateMachine

@InstallIn(ViewModelComponent::class)
@Module
class StateMachineModule {

    @Provides
    fun provideStateMachine(): StateMachine<State, Event> {
        return StateMachine()
    }

}