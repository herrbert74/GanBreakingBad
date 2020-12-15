package com.babestudios.ganbb

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.babestudios.ganbb.data.network.GanBbRepositoryContract
import com.babestudios.ganbb.model.Character
import com.babestudios.ganbb.navigation.GanBbNavigator
import com.babestudios.ganbb.ui.characters.CharactersFragmentDirections
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.get
import kotlinx.coroutines.launch

class GanBbViewModel @ViewModelInject constructor(
    private val ganBbRepository: GanBbRepositoryContract,
    private val ganBbNavigator: GanBbNavigator,
) : ViewModel() {

    //state
    private var queryText = ""
    private var filterSeasonOrdinal = 0
    private var fullCharacterList: List<Character> = emptyList()

    //exposed state
    var charactersLiveData = MutableLiveData<Result<List<Character>, Throwable>>()
    var characterLiveData = MutableLiveData<Character?>()

    fun bindNavController(navController: NavController) {
        ganBbNavigator.bind(navController)
    }

    fun popBackStack() {
        ganBbNavigator.popBackStack()
    }

    fun loadCharacters() {
        viewModelScope.launch {
            val result = ganBbRepository.getCharacters()
            charactersLiveData.value = result
            fullCharacterList = result.get() ?: emptyList()
        }
    }

    fun navigateToCharacterDetails(id: Long) {
        CharactersFragmentDirections.actionCharactersFragmentToCharacterDetailsFragment()
        ganBbNavigator.mainToCharacterDetails(id)
    }

    fun getCharacterById(id: Long) {
        val characters = charactersLiveData.value
        val character = characters?.fold(
            { it.first { character -> character.id == id } },
            { null }
        )
        characterLiveData.postValue(character)
    }

//region Search

    fun onSearchQueryChanged(queryTextChanged: String) {
        queryText = queryTextChanged
        applyFilters()
    }

    fun clearSearch() {
        queryText = ""
        applyFilters()
    }

    fun setSeasonFilter(seasonOrdinal: Int) {
        filterSeasonOrdinal = seasonOrdinal
        applyFilters()
    }

    private fun applyFilters() {
        if (queryText.length > 2 || filterSeasonOrdinal > 0) {
            charactersLiveData.postValue(Ok(fullCharacterList.filter {
                it.name.contains(
                    queryText,
                    true
                ) && (filterSeasonOrdinal == 0 ||
                        it.appearance.contains(filterSeasonOrdinal.toString()))
            }))
        } else {
            charactersLiveData.postValue(Ok(fullCharacterList))
        }
    }

    //endregion
}
