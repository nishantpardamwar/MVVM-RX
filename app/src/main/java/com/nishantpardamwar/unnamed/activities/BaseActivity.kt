package com.nishantpardamwar.unnamed.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nishantpardamwar.unnamed.states.BaseState
import com.nishantpardamwar.unnamed.viewmodels.BaseViewModel
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseActivity<ViewModel : BaseViewModel<State>, State : BaseState> : AppCompatActivity() {
    private var disposable: Disposable? = null
    lateinit var viewModel: ViewModel
    abstract val layoutResourceId: Int

    abstract fun injectDependencies()
    abstract fun initView()
    abstract fun renderStates(state: State)

    private fun bindViewModel() {
        viewModel.attachLifeCycle(lifecycle)
        disposable?.dispose()
        disposable = viewModel.observeStates().retry().subscribe({ state ->
            Log.d(localClassName, "new state ${state.javaClass.name}")
            renderStates(state)
        }, { error ->
            error.printStackTrace()
        })
    }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        injectDependencies()
        bindViewModel()
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
