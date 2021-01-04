package com.nishantpardamwar.unnamed.network

import com.google.gson.JsonObject
import com.nishantpardamwar.unnamed.network.responses.SearchResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

import retrofit2.http.Query

interface WikipediaServices {
    companion object {
        const val BASE_URL = "https://en.wikipedia.org"
    }

    @GET("/w/api.php?action=query&format=json&list=search")
    fun searchQuery(
        @Query("srsearch") query: String,
        @Query("srlimit") resultPerPage: Int
    ): Single<SearchResponse>

    @GET("/w/api.php?action=query&format=json&list=search")
    fun searchLoadMore(
        @Query("srsearch") query: String,
        @Query("sroffset") previousOffset: Int,
        @Query("continue") continueString: String
    ): Single<SearchResponse>
}