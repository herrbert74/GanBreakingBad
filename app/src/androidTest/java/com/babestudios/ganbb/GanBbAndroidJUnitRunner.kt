package com.babestudios.ganbb

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Use our TestApplication to inject dependencies
 */

class GanBbAndroidJUnitRunner : AndroidJUnitRunner() {

	@Throws(
		InstantiationException::class,
		IllegalAccessException::class,
		ClassNotFoundException::class
	)
	override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
		return super.newApplication(cl, HiltTestApplication::class.java.name, context)
	}
}
