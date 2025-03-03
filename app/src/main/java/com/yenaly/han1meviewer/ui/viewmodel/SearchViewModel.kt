package com.yenaly.han1meviewer.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.yenaly.han1meviewer.logic.DatabaseRepo
import com.yenaly.han1meviewer.logic.NetworkRepo
import com.yenaly.han1meviewer.logic.model.HanimeInfoModel
import com.yenaly.han1meviewer.logic.entity.SearchHistoryEntity
import com.yenaly.han1meviewer.logic.model.SearchTagModel
import com.yenaly.han1meviewer.logic.state.PageLoadingState
import com.yenaly.han1meviewer.logic.state.WebsiteState
import com.yenaly.yenaly_libs.base.YenalyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @project Hanime1
 * @author Yenaly Liew
 * @time 2022/06/13 013 22:29
 */
class SearchViewModel(application: Application) : YenalyViewModel(application) {

    var page: Int = 1
    var query: String? = null

    // START: Use in [ChildCommentPopupFragment.kt]

    var genre: String? = null
    var sort: String? = null
    var broad: Boolean = false
    var year: Int? = null
    var month: Int? = null
    var duration: String? = null
    val tagSet = mutableSetOf<String>()
    val brandSet = mutableSetOf<String>()

    // END: Use in [ChildCommentPopupFragment.kt]

    private val _searchFlow =
        MutableStateFlow<PageLoadingState<MutableList<HanimeInfoModel>>>(PageLoadingState.Loading)
    val searchFlow = _searchFlow.asStateFlow()

    private val _searchTagFlow =
        MutableStateFlow<WebsiteState<SearchTagModel>>(WebsiteState.Loading)
    val searchTagFlow = _searchTagFlow.asStateFlow()

    fun getHanimeSearchResult(
        page: Int, query: String?, genre: String?,
        sort: String?, broad: Boolean, year: Int?, month: Int?,
        duration: String?, tags: Set<String>, brands: Set<String>,
    ) {
        viewModelScope.launch {
            NetworkRepo.getHanimeSearchResult(
                page, query, genre,
                sort, broad, year, month,
                duration, tags, brands
            ).collect { search ->
                _searchFlow.emit(search)
            }
        }
    }

    fun getHanimeSearchTags() {
        viewModelScope.launch {
            NetworkRepo.getHanimeSearchTags().collect { tags ->
                _searchTagFlow.value = tags
            }
        }
    }

    fun insertSearchHistory(history: SearchHistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseRepo.SearchHistory.insert(history)
            Log.d("insert_search_hty", "$history DONE!")
        }
    }

    fun deleteSearchHistory(history: SearchHistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseRepo.SearchHistory.delete(history)
            Log.d("delete_search_hty", "$history DONE!")
        }
    }

    @JvmOverloads
    fun loadAllSearchHistories(keyword: String? = null) =
        DatabaseRepo.SearchHistory.loadAll(keyword).flowOn(Dispatchers.IO)

    fun deleteSearchHistoryByKeyword(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseRepo.SearchHistory.deleteByKeyword(query)
            Log.d("delete_search_hty", "$query DONE!")
        }
    }
}