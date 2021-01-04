package com.nishantpardamwar.unnamed.viewmodels

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.nishantpardamwar.unnamed.rx.FlowableTransformers
import com.nishantpardamwar.unnamed.states.BaseState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.reactivestreams.Subscription

abstract class BaseViewModel<State : BaseState> : ViewModel(), LifecycleObserver {
    private var lifecycle: Lifecycle? = null
    private val lifeCycleValve: BehaviorProcessor<Boolean> = BehaviorProcessor.create()
    private val states: BehaviorSubject<State> = BehaviorSubject.create()
    private var subscription: Subscription? = null

    fun observeStates(): Flowable<State> {
        return states.toFlowable(BackpressureStrategy.BUFFER)
            .compose(FlowableTransformers.valve(lifeCycleValve))
            .doOnSubscribe {
                subscription = it
            }
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun attachLifeCycle(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle
        this.lifecycle?.addObserver(this)
    }

    fun postState(state: State) {
        states.onNext(state)
    }

    override fun onCleared() {
        super.onCleared()
        this.lifecycle?.removeObserver(this)
        subscription?.cancel()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        lifeCycleValve.onNext(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        lifeCycleValve.onNext(false)
    }
}