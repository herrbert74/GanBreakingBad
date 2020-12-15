package com.babestudios.ganbb.navigation

import com.babestudios.ganbb.common.navigation.Navigator

interface GanBbNavigator : Navigator {
	fun mainToCharacterDetails(id: Long)
}
