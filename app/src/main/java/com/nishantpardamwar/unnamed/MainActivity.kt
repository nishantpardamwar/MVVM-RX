package com.nishantpardamwar.unnamed

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.nishantpardamwar.unnamed.activities.BaseActivity
import com.nishantpardamwar.unnamed.states.MainActivityStates
import com.nishantpardamwar.unnamed.viewmodels.MainActivityViewModel
import com.nishantpardamwar.unnamed.viewmodels.VMFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainActivityViewModel, MainActivityStates>() {
    private val adapter = Adapter(onLoadMore = {
        viewModel.searchLoadMore(searchBox.text.toString())
    })

    override val layoutResourceId = R.layout.activity_main

    override fun injectDependencies() {
        viewModel = ViewModelProvider(this, VMFactory).get(MainActivityViewModel::class.java)
    }

    override fun initView() {
        listRecyclerView.adapter = adapter
        listRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val currentPosition = parent.getChildAdapterPosition(view)
                if (currentPosition == 0) {
                    outRect.top = Utils.dp2px(45)
                } else {
                    outRect.top = 0
                }
            }
        })
        searchBox.doOnTextChanged { text, start, count, after ->
            viewModel.searchQuery(text.toString())
        }
    }

    override fun renderStates(state: MainActivityStates) {
        when (state) {
            is MainActivityStates.KeyboardState -> searchBox.keyboardVisibility(state.show)
            is MainActivityStates.LoaderState -> loader.isVisible = state.show
            is MainActivityStates.LoadingMoreLoaderState -> handleLoadingMoreLoader(state.show)
            is MainActivityStates.ListState -> adapter.setNewList(state.list, state.hasMore)
            is MainActivityStates.ListLoadMoreState -> adapter.loadMore(state.list, state.hasMore)
            is MainActivityStates.ErrorState -> Toast.makeText(this, state.errorMessage ?: "error hmm!!", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun handleLoadingMoreLoader(show: Boolean) {
        if (show) {
            loaderLoadingMore.animate().translationYBy(-Utils.dp2px(35).toFloat()).setDuration(100)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        loaderLoadingMore.isVisible = true
                    }
                })
                .start()
        } else {
            loaderLoadingMore.animate().translationYBy(Utils.dp2px(35).toFloat()).setDuration(100)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        loaderLoadingMore.isVisible = false
                    }
                })
                .start()
        }
    }
}