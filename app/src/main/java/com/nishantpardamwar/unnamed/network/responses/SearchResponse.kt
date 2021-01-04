package com.nishantpardamwar.unnamed.network.responses

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("continue")
    val searchContinue: SearchContinue?,
    @SerializedName("query")
    val queryResult: SearchQueryResult?
) {

}

data class SearchContinue(
    @SerializedName("sroffset")
    val sroffset: Int?,
    @SerializedName("continue")
    val continueString: String?
)

data class SearchQueryResult(
    @SerializedName("searchinfo")
    val searchInfo: SearchInfo?,
    @SerializedName("search")
    val searchList: List<SearchItem>?
)

data class SearchItem(
    @SerializedName("title")
    val title: String?,
    @SerializedName("snippet")
    val snippet: String?,
    @SerializedName("pageid")
    val pageid: Long?
)

data class SearchInfo(
    @SerializedName("totalhits")
    val totalResult: Int?
)