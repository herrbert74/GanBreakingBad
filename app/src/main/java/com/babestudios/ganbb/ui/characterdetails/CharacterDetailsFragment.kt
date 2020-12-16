package com.babestudios.ganbb.ui.characterdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.babestudios.ganbb.GanBbViewModel
import com.babestudios.ganbb.databinding.FragmentCharacterDetailsBinding
import com.babestudios.ganbb.model.Character
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CharacterDetailsFragment : Fragment() {

    private val ganBbViewModel: GanBbViewModel by activityViewModels()

    private var _binding: FragmentCharacterDetailsBinding? = null
    private val binding get() = _binding!!

    //region life cycle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = requireArguments()
        val id = args.getLong("id")
        initializeUI(id)
    }

    private fun initializeUI(id: Long) {
        (activity as AppCompatActivity).setSupportActionBar(binding.tbCharacterDetails)
        val toolBar = (activity as AppCompatActivity).supportActionBar
        toolBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbCharacterDetails.setNavigationOnClickListener { activity?.onBackPressed() }
        ganBbViewModel.characterLiveData.observe(viewLifecycleOwner) { character ->
            character?.let {
                toolBar?.title = character.name
                showCharacter(character)
            }
        }
        ganBbViewModel.getCharacterById(id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //endregion

    //region render

    private fun showCharacter(character: Character) {
        Glide.with(requireContext())
            .load(character.img)
            .into(binding.ivCharacterDetails)
        binding.lblCharacterDetailsName.text = character.name
        binding.lblCharacterDetailsOccupation.text = character.occupation
        binding.lblCharacterDetailsStatus.text = character.status.value
        binding.lblCharacterDetailsNickname.text = character.nickname
        binding.lblCharacterDetailsAppearances.text = character.appearance
    }

//endregion

}
