package com.babestudios.ganbb.navigation

import androidx.navigation.NavController
import com.babestudios.ganbb.common.navigation.BaseNavigator
import com.babestudios.ganbb.common.navigation.navigateSafe
import com.babestudios.ganbb.ui.characters.CharactersFragmentDirections

class GanBbNavigation : BaseNavigator(), GanBbNavigator {

	override var navController: NavController? = null

	override fun mainToCharacterDetails(id: Long) {
		val action =
			CharactersFragmentDirections.actionCharactersFragmentToCharacterDetailsFragment(id)
		navController?.navigateSafe(action)
	}
}
