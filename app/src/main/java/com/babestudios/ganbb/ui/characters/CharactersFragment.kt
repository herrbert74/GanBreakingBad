package com.babestudios.ganbb.ui.characters

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.babestudios.base.ext.textColor
import com.babestudios.base.view.DividerItemDecoration
import com.babestudios.base.view.FilterAdapter
import com.babestudios.base.view.MultiStateView.*
import com.babestudios.ganbb.GanBbViewModel
import com.babestudios.ganbb.R
import com.babestudios.ganbb.common.network.OfflineException
import com.babestudios.ganbb.databinding.FragmentCharactersBinding
import com.babestudios.ganbb.model.Character
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.groupiex.plusAssign
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.android.widget.itemSelections
import reactivecircus.flowbinding.android.widget.textChanges

const val SEARCH_QUERY_MIN_LENGTH = 3

@AndroidEntryPoint
class CharactersFragment : Fragment() {

    private val ganBbViewModel: GanBbViewModel by activityViewModels()

    private var _binding: FragmentCharactersBinding? = null
    private val binding get() = _binding!!

    private var groupAdapter = GroupAdapter<GroupieViewHolder>()

    private var charactersSection = Section()

    var firstVisibleItemPosition: Int = 0

    //Menu
    lateinit var searchMenuItem: MenuItem
    private var filterMenuItem: MenuItem? = null
    private var lblSearch: TextView? = null
    private var flagDoNotAnimateSearchMenuItem = false
    private lateinit var spinner: Spinner

    private var searchToolbarAnimationDuration: Long = 0

    //state
    var isSearchMenuItemExpanded = false

    //region life cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharactersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchToolbarAnimationDuration =
            resources.getInteger(R.integer.search_toolbar_animation_duration).toLong()
        initializeUI()
    }

    private fun initializeUI() {
        (activity as AppCompatActivity).setSupportActionBar(binding.tbCharacters)
        createSearchRecyclerView()

        ganBbViewModel.charactersLiveData.observe(requireActivity()) { result ->
            result.onSuccess {
                showSearchResults(it)
            }
            result.onFailure {
                showSearchError(it)
            }
        }

        groupAdapter.setOnItemClickListener { item, _ ->
            ganBbViewModel.navigateToCharacterDetails((item as CharacterItem).character.id)
        }

        if (groupAdapter.itemCount == 0) {
            ganBbViewModel.loadCharacters()
        }

        binding.btnCharactersReload.clicks().onEach {
            binding.llCharactersCannotConnect.visibility = View.GONE
            ganBbViewModel.loadCharacters()
        }.launchIn(lifecycleScope)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*override fun orientationChanged() {
        val activity = requireActivity() as LastFmAlbumsActivity
        viewModel.setNavigator(activity.injectLastFmNavigator())
    }*/

    private fun createSearchRecyclerView() {
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvCharacters.layoutManager = linearLayoutManager
        binding.rvCharacters.addItemDecoration(DividerItemDecoration(requireContext()))
        binding.rvCharacters.adapter = groupAdapter
        binding.rvCharacters.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                firstVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            }
        })
        binding.rvCharacters.scrollToPosition(firstVisibleItemPosition)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val item = menu.findItem(R.id.action_filter)
        setupFilterSpinner(item)
        searchMenuItem = menu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as SearchView
        lblSearch =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as TextView?
        lblSearch?.hint = "Search"
        lblSearch?.textColor = ContextCompat.getColor(requireContext(), android.R.color.black)
        lblSearch?.textChanges()?.debounce(
            resources.getInteger(R.integer.search_input_field_debounce).toLong()
        )?.onEach {
            ganBbViewModel.onSearchQueryChanged(lblSearch?.text.toString())
        }?.launchIn(lifecycleScope)
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                if (searchMenuItem.isActionViewExpanded) {
                    animateSearchToolbar(1, containsOverflow = false, show = false)
                }
                filterMenuItem?.isVisible = false
                searchMenuItem.isVisible = true
                isSearchMenuItemExpanded = false
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                if (!flagDoNotAnimateSearchMenuItem) {
                    animateSearchToolbar(1, containsOverflow = true, show = true)
                    isSearchMenuItemExpanded = true
                } else {
                    flagDoNotAnimateSearchMenuItem = false
                }
                filterMenuItem?.isVisible = true
                return true
            }
        })
        //For when invalidateOptionsMenu called
        if (isSearchMenuItemExpanded) {
            searchMenuItem.expandActionView()
            (searchMenuItem.actionView as SearchView).setQuery(lblSearch?.text, false)
            flagDoNotAnimateSearchMenuItem = true
        }
        //For when we are recovering after a process death
        if (!lblSearch?.text.isNullOrEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed({
                //To avoid skipping initial state in this case : we want to reload it
                searchMenuItem.expandActionView()
                lblSearch?.text = lblSearch?.text
            }, searchToolbarAnimationDuration)
        }
    }

    private fun setupFilterSpinner(item: MenuItem) {
        spinner = item.actionView as Spinner
        spinner.setBackgroundResource(0)
        spinner.setPadding(0, 0, resources.getDimensionPixelOffset(R.dimen.screenMargin), 0)
        spinner.gravity = Gravity.END
        val adapter = FilterAdapter(
            requireContext(),
            resources.getStringArray(R.array.season_filter_items),
            isDropdownDarkTheme = true,
            isToolbarDarkTheme = true
        )
        spinner.adapter = adapter
        /*withState(viewModel) { state ->
            if (state.filingCategoryFilter != Category.CATEGORY_SHOW_ALL) {
                spinner.setSelection(state.filingCategoryFilter.ordinal)
            }
        }*/
        if (::spinner.isInitialized) {
            spinner.itemSelections().skipInitialValue().onEach {
                ganBbViewModel.setSeasonFilter(it)
            }.launchIn(lifecycleScope)
        }
    }

    @SuppressLint("PrivateResource")
    fun animateSearchToolbar(numberOfMenuIcon: Int, containsOverflow: Boolean, show: Boolean) {

        binding.tbCharacters.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.white
            )
        )

        if (show) {
            binding.msvCharacters.visibility = View.VISIBLE
            binding.msvCharacters.viewState = VIEW_STATE_CONTENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val width = binding.tbCharacters.width -
                        if (containsOverflow)
                            resources.getDimensionPixelSize(
                                R.dimen.abc_action_button_min_width_overflow_material
                            )
                        else
                            (0) - ((resources.getDimensionPixelSize(
                                R.dimen.abc_action_button_min_width_material
                            ) * numberOfMenuIcon) / 2)
                val createCircularReveal = ViewAnimationUtils.createCircularReveal(
                    binding.tbCharacters,
                    if (isRtl(resources)) binding.tbCharacters.width - width else width,
                    binding.tbCharacters.height / 2,
                    0.0f,
                    width.toFloat()
                )
                createCircularReveal.duration = searchToolbarAnimationDuration
                createCircularReveal.start()
            } else {
                val translateAnimation = TranslateAnimation(
                    0.0f,
                    0.0f,
                    ((-binding.tbCharacters.height).toFloat()),
                    0.0f
                )
                translateAnimation.duration = searchToolbarAnimationDuration
                binding.tbCharacters.clearAnimation()
                binding.tbCharacters.startAnimation(translateAnimation)
            }
        } else {
            ganBbViewModel.clearSearch()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val width = binding.tbCharacters.width -
                        if (containsOverflow)
                            resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material)
                        else
                            0 - ((resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)
                                    * numberOfMenuIcon) / 2)
                val createCircularReveal = ViewAnimationUtils.createCircularReveal(
                    binding.tbCharacters,
                    if (isRtl(resources)) binding.tbCharacters.width - width else width,
                    binding.tbCharacters.height / 2,
                    width.toFloat(),
                    0.0f
                )
                createCircularReveal.duration = searchToolbarAnimationDuration
                createCircularReveal.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        binding.tbCharacters.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.purple_500
                            )
                        )
                        activity?.invalidateOptionsMenu()
                    }
                })
                createCircularReveal.start()
            } else {
                val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
                val translateAnimation =
                    TranslateAnimation(0.0f, 0.0f, 0.0f, -binding.tbCharacters.height.toFloat())
                val animationSet = AnimationSet(true)
                animationSet.addAnimation(alphaAnimation)
                animationSet.addAnimation(translateAnimation)
                animationSet.duration = searchToolbarAnimationDuration
                animationSet.setAnimationListener(object : Animation.AnimationListener {
                    @Suppress("EmptyFunctionBlock")
                    override fun onAnimationStart(animation: Animation) {

                    }

                    override fun onAnimationEnd(animation: Animation) {
                        binding.tbCharacters.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.purple_500
                            )
                        )
                        activity?.invalidateOptionsMenu()
                    }

                    @Suppress("EmptyFunctionBlock")
                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
                binding.tbCharacters.startAnimation(animationSet)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun isRtl(resources: Resources): Boolean {
        return resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
    }

    //endregion

    //region render

    private fun showSearchResults(characterList: List<Character>) {
        val tvMsvSearchEmpty = binding.msvCharacters.findViewById<TextView>(R.id.tvMsvEmpty)
        val queryTextLength = lblSearch?.text?.length ?: 0
        if (queryTextLength >= SEARCH_QUERY_MIN_LENGTH && characterList.isEmpty()) {
            binding.msvCharacters.viewState = VIEW_STATE_EMPTY
            tvMsvSearchEmpty.text =
                getString(R.string.no_search_result)
        } else {
            if (queryTextLength > 2) {
                binding.rvCharacters.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.white
                    )
                )
            } else
                binding.rvCharacters.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.semiTransparentBlack
                    )
                )
            showContent(characterList)
        }
    }

    private fun showContent(characterList: List<Character>) {
        binding.msvCharacters.viewState = VIEW_STATE_CONTENT
        if (groupAdapter.itemCount == 0) {
            charactersSection = Section()
            charactersSection.apply {
                for (element in characterList) {
                    add(CharacterItem(element))
                }
            }
            groupAdapter += charactersSection
        } else {
            charactersSection.update(characterList.map { CharacterItem(it) })
        }
        if (binding.rvCharacters.adapter == null) {
            binding.rvCharacters.adapter = groupAdapter
        }
    }

    private fun showSearchError(throwable: Throwable) {
        if (throwable is OfflineException) {
            showContent((throwable.obj as? List<Character>) ?: emptyList())
            binding.llCharactersCannotConnect.visibility = View.VISIBLE
        } else {
            val tvMsvSearchError = binding.msvCharacters.findViewById<TextView>(R.id.tvMsvError)
            binding.msvCharacters.viewState = VIEW_STATE_ERROR
            tvMsvSearchError.text = throwable.message
        }
    }

    private fun showLoading() {
        binding.msvCharacters.viewState = VIEW_STATE_LOADING
    }

//endregion

//region events

    /*@Suppress("LongMethod")
    private fun observeActions() {
        eventDisposables.clear()
        lblSearch?.let {
            RxTextView.textChanges(it)
                .skipInitialValue()
                .debounce(
                    resources.getInteger(R.integer.search_input_field_debounce).toLong(),
                    TimeUnit.MILLISECONDS
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    viewModel.onSearchQueryChanged(lblSearch?.text.toString())
                }
                ?.let { queryTextChangeDisposable -> eventDisposables.add(queryTextChangeDisposable) }
        }
        searchAdapter?.getViewClickedObservable()
            //?.take(1)
            ?.subscribe { view: BaseViewHolder<AbstractSearchVisitable> ->
                withState(viewModel) { state ->
                    state.searchVisitables.let {
                        viewModel.searchItemClicked(view.adapterPosition)
                    }
                }
            }
            ?.let { eventDisposables.add(it) }
    }*/

//endregion

}
