package com.nishantpardamwar.unnamed.states

import com.nishantpardamwar.unnamed.network.responses.SearchContinue
import com.nishantpardamwar.unnamed.network.responses.SearchItem

sealed class MainActivityStates : BaseState {
    data class LoaderState(val show: Boolean) : MainActivityStates()
    data class LoadingMoreLoaderState(val show: Boolean) : MainActivityStates()
    data class KeyboardState(val show: Boolean) : MainActivityStates()
    data class ListState(val list: List<SearchItem>, val hasMore: Boolean) : MainActivityStates()
    data class ListLoadMoreState(val list: List<SearchItem>, val hasMore: Boolean) : MainActivityStates()
    data class ErrorState(val errorMessage: String? = null, val error: Throwable? = null) : MainActivityStates()
}