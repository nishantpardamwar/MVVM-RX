package com.nishantpardamwar.unnamed.viewmodels

import com.nishantpardamwar.unnamed.network.responses.SearchContinue
import com.nishantpardamwar.unnamed.network.responses.SearchResponse
import com.nishantpardamwar.unnamed.repositories.Repository
import com.nishantpardamwar.unnamed.states.MainActivityStates
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class MainActivityViewModel(private val repository: Repository) : BaseViewModel<MainActivityStates>() {
    private var currentSearchResponse: SearchResponse? = null
    fun searchQuery(query: String) {
        postState(MainActivityStates.LoaderState(true))
        repository.searchQuery(query).subscribe({ result ->
            currentSearchResponse = result
            if (result.queryResult?.searchList != null) {
                postState(MainActivityStates.ListState(result.queryResult.searchList, result.searchContinue != null))
                postState(MainActivityStates.KeyboardState(false))
            } else {
                postState(MainActivityStates.ErrorState("No response available"))
            }
            postState(MainActivityStates.LoaderState(false))
        }, { error ->
            postState(MainActivityStates.ErrorState(error.message, error))
            postState(MainActivityStates.LoaderState(false))
        })
    }

    fun searchLoadMore(query: String) {
        currentSearchResponse?.searchContinue?.let { searchContinue ->
            postState(MainActivityStates.LoadingMoreLoaderState(true))
            repository.searchLoadMore(query, searchContinue).subscribe({ result ->
                currentSearchResponse = result
                if (result.queryResult?.searchList != null) {
                    postState(MainActivityStates.ListLoadMoreState(result.queryResult.searchList, result.searchContinue != null))
                    postState(MainActivityStates.KeyboardState(false))
                } else {
                    postState(MainActivityStates.ErrorState("No response available"))
                }
                postState(MainActivityStates.LoadingMoreLoaderState(false))
            }, { error ->
                postState(MainActivityStates.ErrorState(error.message, error))
                postState(MainActivityStates.LoadingMoreLoaderState(false))
            })
        }
    }
}