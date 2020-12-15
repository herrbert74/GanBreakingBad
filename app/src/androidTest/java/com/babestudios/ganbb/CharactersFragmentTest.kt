package com.babestudios.ganbb

import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.babestudios.ganbb.di.NetworkModule
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertListItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaListInteractions
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@UninstallModules(NetworkModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CharactersFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun populateRecyclerViewTest() {
        launchActivity<MainActivity>()
        assertListItemCount(R.id.rvCharacters, 63)
    }

    @Test
    fun startDetailsTest() {
        launchActivity<MainActivity>()
        BaristaListInteractions.clickListItem(R.id.rvCharacters, 0)
        assertDisplayed(R.id.lblCharacterDetailsName, "Walter White")
    }
}