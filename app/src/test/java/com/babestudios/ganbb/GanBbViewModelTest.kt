package com.babestudios.ganbb

import com.babestudios.ganbb.data.network.GanBbRepositoryContract
import com.babestudios.ganbb.data.network.characterDtoMapper
import com.babestudios.ganbb.data.network.dto.CharacterDto
import com.babestudios.ganbb.model.Character
import com.babestudios.ganbb.navigation.GanBbNavigator
import com.babestudios.ganbb.testhelpers.JsonHelper
import com.github.michaelbull.result.Ok
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GanBbViewModelTest : BaseViewModelTest() {

	private val ganBbRepositoryContract = mockk<GanBbRepositoryContract>(relaxed = true)

	private val ganBbNavigator = mockk<GanBbNavigator>()

	private var mappedResponse :List<Character> = emptyList()

	private var viewModel: GanBbViewModel? = null

	@Before
	fun setUp() {
		val response = JsonHelper().loadJson("bb_characters_response")
		val gson = Gson()
		val itemType = object : TypeToken<List<CharacterDto>>() {}.type
		val responseDto = gson.fromJson<List<CharacterDto>>(response, itemType)
		mappedResponse = characterDtoMapper(responseDto)
		every {
			ganBbNavigator.mainToCharacterDetails(any())
		} answers
				{
					Exception("")
				}
		coEvery {
			ganBbRepositoryContract.getCharacters()
		} answers {
			Ok(mappedResponse)
		}
		viewModel = ganBbViewModel()
	}

	@Test
	fun `when loadCharacters is called then liveData is posted`() {
		viewModel?.loadCharacters()
		(viewModel?.charactersLiveData?.value as Ok).value.size shouldBe 63
	}

	@Test
	fun `when search is called then result count is correct`() {
		viewModel?.loadCharacters()
		viewModel?.onSearchQueryChanged("Walter")
		(viewModel?.charactersLiveData?.value as Ok).value.size shouldBe 2
	}

	@Test
	fun `when filter is changed then result count is correct`() {
		viewModel?.loadCharacters()
		viewModel?.setSeasonFilter(1)
		(viewModel?.charactersLiveData?.value as Ok).value.size shouldBe 26
	}

	@Test
	fun `given that search result is applied when search was reset then result count is correct`() {
		viewModel?.loadCharacters()
		viewModel?.onSearchQueryChanged("Walter")
		viewModel?.clearSearch()
		(viewModel?.charactersLiveData?.value as Ok).value.size shouldBe 63
	}

	@Test
	fun `when character item is clicked then navigator is called`() {
		viewModel?.navigateToCharacterDetails(1)
		verify(exactly = 1) {
			ganBbNavigator.mainToCharacterDetails(1)
		}
	}

	@Test
	fun `when character by id is requested then proper character is responded`() {
		viewModel?.loadCharacters()
		viewModel?.getCharacterById(1)
		viewModel?.characterLiveData?.value?.name shouldBe "Walter White"
	}

	private fun ganBbViewModel(): GanBbViewModel {
		return GanBbViewModel(
			ganBbRepositoryContract,
			ganBbNavigator,
		)
	}
}
