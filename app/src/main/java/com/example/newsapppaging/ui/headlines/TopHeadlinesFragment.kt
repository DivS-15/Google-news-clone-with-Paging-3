package com.example.newsapppaging.ui.headlines

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.newsapppaging.R
import com.example.newsapppaging.data.model.Article
import com.example.newsapppaging.data.model.HeadlinesCategory
import com.example.newsapppaging.databinding.TopHeadlinesBinding
import com.example.newsapppaging.ui.NewsViewModel
import com.example.newsapppaging.ui.loadstate_list_item.NewsLoadStateAdapter
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TopHeadlinesFragment"

@AndroidEntryPoint
class TopHeadlinesFragment : Fragment(R.layout.top_headlines) {
    private var _binding: TopHeadlinesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var category: HeadlinesCategory

    private val viewModel: NewsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = TopHeadlinesBinding.bind(view)
        val adapter = TopHeadLinesAdapter(viewLifecycleOwner.lifecycleScope)

        binding.apply {
            newsSearchList.setHasFixedSize(true)
            newsSearchList.adapter = adapter.withLoadStateHeaderAndFooter(
                header = NewsLoadStateAdapter { adapter.retry() },
                footer = NewsLoadStateAdapter { adapter.retry() }
            )
            swipeRefresh.setOnRefreshListener {
                adapter.retry()
            }
            retryButton.setOnClickListener {
                adapter.retry()
            }

        }

        adapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                combinedLoadStates.apply {
                    newsSearchList.isVisible = this.source.refresh is LoadState.NotLoading
                    progressBar.isVisible = this.source.refresh is LoadState.Loading
                    retryButton.isVisible = this.source.refresh is LoadState.Error
                    textRetryRequest.isVisible = this.source.refresh is LoadState.Error
                    //swipeRefresh.isRefreshing = this.source.refresh is LoadState.Loading
                }

            }
        }
        setHasOptionsMenu(false)




        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.saveArticlesFlow.collectLatest {
                when (it) {
                    is SaveArticlesInfo.ArticlesSaveEvent -> {
                        viewModel.onFavouriteClicked(it.article)
                        Snackbar.make(requireView(), "Added to your Saved Articles", LENGTH_LONG)
                            .setBackgroundTint(resources.getColor(R.color.purple_700))
                            .setTextColor(resources.getColor(R.color.white))
                            .setAnchorView(R.id.bottom_nav)
                            .show()
                    }
                }
            }
        }

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.newsSearchList.addItemDecoration(dividerItemDecoration)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.topHeadlinesFlow.collectLatest { value: PagingData<Article> ->
                    adapter.submitData(value)
                    adapter.retry()
                    Log.d(TAG, value.toString())
                }
            }

        }


        /**********************************************************************************
        Populating different Tabs by submitting preferences to ViewModel
         **********************************************************************************
         */

        category = HeadlinesCategory()

        arguments?.takeIf {
            it.containsKey("tab").apply {
                val requiredTab = it.getInt("tab")

                category.apply {
                    if (requiredTab == 1) {
                        viewModel.topHeadlinesPreferences("en", " ", " ")
                        adapter.retry()
                    }

                    if (requiredTab == 2) {
                        viewModel.topHeadlinesPreferences(" ", "in", " ")
                        adapter.retry()
                    }

                    if (requiredTab == 3) {
                        viewModel.topHeadlinesPreferences("en", " ", business)
                        adapter.retry()
                    }

                    if (requiredTab == 4) {
                        viewModel.topHeadlinesPreferences("en", " ", entertainment)
                        adapter.retry()
                    }

                    if (requiredTab == 5) {
                        viewModel.topHeadlinesPreferences("en", " ", general)
                        adapter.retry()
                    }

                    if (requiredTab == 6) {
                        viewModel.topHeadlinesPreferences("en", " ", health)
                        adapter.retry()
                    }

                    if (requiredTab == 7) {
                        viewModel.topHeadlinesPreferences("en", " ", science)
                        adapter.retry()
                    }

                    if (requiredTab == 8) {
                        viewModel.topHeadlinesPreferences("en", " ", sports)
                        adapter.retry()
                    }

                    if (requiredTab == 9) {
                        viewModel.topHeadlinesPreferences("en", " ", technology)
                        adapter.retry()
                    }
                }


            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}