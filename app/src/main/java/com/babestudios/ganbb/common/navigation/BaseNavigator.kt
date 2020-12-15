package com.babestudios.ganbb.common.navigation

import androidx.navigation.NavController

/**
 * [bind] currently needs to be called from both onCreate of the Activity and from the inject*Navigator() function.
 * This is because both the Dagger Component and the NavigationController needs to be initialized,
 * but the order of init is different in a normal case and when the Activity is recreated after a configuration change.
 * In the latter case the ViewModel is created and injected before onCreate() finishes.
 *
 * To avoid crashes use something like "::comp.isLazyInitialized" in OnCreate
 * and "::navController.isInitialized" in the provider.
 *
 * Also important is to save lateinit references to the [Navigator] and the [navController],
 * so these are not null in the ViewModel.
 */
interface Navigator {
	var navController: NavController?
	fun bind(navController: NavController)
	fun unbind()
	fun popBackStack()
}

abstract class BaseNavigator : Navigator {

	override fun bind(navController: NavController) {
		this.navController = navController
	}

	override fun unbind() {
		navController = null
	}

	override fun popBackStack() {
		navController?.popBackStack()
	}
}
