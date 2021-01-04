package com.nishantpardamwar.unnamed.repositories

import android.content.Context
import com.nishantpardamwar.unnamed.NetworkClient
import com.nishantpardamwar.unnamed.network.responses.SearchContinue
import com.nishantpardamwar.unnamed.network.responses.SearchResponse
import io.reactivex.rxjava3.core.Single

class Repository(private val context: Context, private val networkClient: NetworkClient) {
    fun searchQuery(query: String): Single<SearchResponse> {
        return networkClient.services.searchQuery(query, 15)
    }

    fun searchLoadMore(query: String, searchContinue: SearchContinue): Single<SearchResponse> {
        val previousOffset = searchContinue.sroffset ?: 0
        val continueString = searchContinue.continueString ?: "-||"
        return networkClient.services.searchLoadMore(query, previousOffset, continueString)
    }
}