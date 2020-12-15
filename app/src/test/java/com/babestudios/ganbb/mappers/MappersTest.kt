package com.babestudios.ganbb.mappers

import com.babestudios.ganbb.data.network.characterDtoMapper
import com.babestudios.ganbb.data.network.dto.CharacterDto
import com.babestudios.ganbb.data.network.dto.Status
import com.babestudios.ganbb.model.Character
import com.babestudios.ganbb.testhelpers.JsonHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test

class MappersTest {

	private var mappedResponse :List<Character> = emptyList()

	@Before
	fun setup() {
		val response = JsonHelper().loadJson("bb_characters_response")
		val gson = Gson()
		val itemType = object : TypeToken<List<CharacterDto>>() {}.type
		val responseDto = gson.fromJson<List<CharacterDto>>(response, itemType)
		mappedResponse = characterDtoMapper(responseDto)
	}

	@Test
	fun `when there is a response then name is mapped`() {
		mappedResponse[0].name shouldBe "Walter White"
	}

	@Test
	fun `when there is a response then occupation is mapped`() {
		mappedResponse[0].occupation shouldBe "High School Chemistry Teacher, Meth King Pin"
	}

	@Test
	fun `when there is a response then status is mapped`() {
		mappedResponse[0].status shouldBe Status.Unknown
	}

	@Test
	fun `when there is a response then appearance is mapped`() {
		mappedResponse[0].appearance.replace(" ", "") shouldBe "1,2,3,4,5"
	}

}
