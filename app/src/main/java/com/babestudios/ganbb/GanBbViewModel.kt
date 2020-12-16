package com.babestudios.ganbb

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.babestudios.ganbb.data.network.GanBbRepositoryContract
import com.babestudios.ganbb.model.Character
import com.babestudios.ganbb.navigation.GanBbNavigator
import com.babestudios.ganbb.ui.characters.CharactersFragmentDirections
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.get
import kotlinx.coroutines.launch

private const val QUERY_KEY: String = "queryText"

private const val FILTER_SEASON_ORDINAL_KEY: String = "filterSeasonOrdinal"

class GanBbViewModel @ViewModelInject constructor(
    private val ganBbRepository: GanBbRepositoryContract,
    private val ganBbNavigator: GanBbNavigator,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    //state not saved
    private var fullCharacterList: List<Character> = emptyList()

    //exposed state
    var charactersLiveData = MutableLiveData<Result<List<Character>, Throwable>>()
    var characterLiveData = MutableLiveData<Character?>()

    //transient state (for process death)
    private var selectedCharacterId: Long? = null

    fun bindNavController(navController: NavController) {
        ganBbNavigator.bind(navController)
    }

    fun popBackStack() {
        ganBbNavigator.popBackStack()
    }

    fun loadCharacters() {
        viewModelScope.launch {
            ganBbRepository.getCharacters().also {
                charactersLiveData.value = it
                fullCharacterList = it.get() ?: emptyList()
                selectedCharacterId?.let { id ->
                    selectedCharacterId = null
                    getCharacterById(id)
                }
            }
        }
    }

    fun navigateToCharacterDetails(id: Long) {
        CharactersFragmentDirections.actionCharactersFragmentToCharacterDetailsFragment()
        ganBbNavigator.mainToCharacterDetails(id)
    }

    fun getCharacterById(id: Long) {
        if (fullCharacterList.isEmpty()) {
            selectedCharacterId = id
            loadCharacters()
        } else {
            postCharacterById(id)
        }
    }

    private fun postCharacterById(id: Long) {
        val character = fullCharacterList.first { character -> character.id == id }
        characterLiveData.postValue(character)
    }

    //region Search

    fun onSearchQueryChanged(queryTextChanged: String) {
        setQueryText(queryTextChanged)
    }

    fun clearSearch() {
        setQueryText("")
    }

    fun setSeasonFilter(seasonOrdinal: Int) {
        setFilterSeasonOrdinal(seasonOrdinal)
    }

    private fun applyFilters() {
        if (getQuery().length > 2 || getFilterSeasonOrdinal() > 0) {
            charactersLiveData.postValue(Ok(fullCharacterList.filter {
                it.name.contains(
                    getQuery(),
                    true
                ) && (getFilterSeasonOrdinal() == 0 ||
                        it.appearance.contains(getFilterSeasonOrdinal().toString()))
            }))
        } else {
            charactersLiveData.postValue(Ok(fullCharacterList))
        }
    }

    //endregion

    //region state

    fun getQuery() = savedStateHandle.get<String>(QUERY_KEY) ?: ""

    private fun setQueryText(query: String) = savedStateHandle.set(QUERY_KEY, query).also {
        applyFilters()
    }

    fun getFilterSeasonOrdinal() = savedStateHandle.get<Int>(FILTER_SEASON_ORDINAL_KEY) ?: 0

    private fun setFilterSeasonOrdinal(filterSeasonOrdinal: Int) = savedStateHandle.set(
        FILTER_SEASON_ORDINAL_KEY, filterSeasonOrdinal
    ).also { applyFilters() }

    //endregion

}
