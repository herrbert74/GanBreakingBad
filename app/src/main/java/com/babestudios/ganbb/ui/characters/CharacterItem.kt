package com.babestudios.ganbb.ui.characters

import android.view.View
import com.babestudios.ganbb.R
import com.babestudios.ganbb.databinding.ItemCharacterBinding
import com.babestudios.ganbb.model.Character
import com.bumptech.glide.Glide
import com.xwray.groupie.viewbinding.BindableItem

class CharacterItem(val character: Character) : BindableItem<ItemCharacterBinding>() {

    override fun getLayout() = R.layout.item_character

    override fun bind(viewBinding: ItemCharacterBinding, position: Int) {
        Glide.with(viewBinding.root).load(character.img).into(viewBinding.ivCharacterItem)
        viewBinding.lblCharacterItemName.text = character.name
    }

    override fun initializeViewBinding(view: View) = ItemCharacterBinding.bind(view)
}