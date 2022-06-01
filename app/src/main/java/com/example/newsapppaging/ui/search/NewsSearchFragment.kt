package com.example.newsapppaging.ui.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.newsapppaging.R
import com.example.newsapppaging.data.Article
import com.example.newsapppaging.data.SortParams
import com.example.newsapppaging.databinding.FragmentNewsSearchListBinding
import com.example.newsapppaging.ui.NewsViewModel
import com.example.newsapppaging.ui.headlines.SaveArticlesInfo
import com.example.newsapppaging.ui.loadstate_list_item.NewsLoadStateAdapter
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "NewsSearchFragment"

@AndroidEntryPoint
class NewsSearchFragment :
    Fragment(R.layout.fragment_news_search_list) {
    private var _binding: FragmentNewsSearchListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentNewsSearchListBinding.bind(view)
        val adapter = NewsAdapter()



        binding.apply {
            newsSearchList.apply {
                itemAnimator = null
                setHasFixedSize(true)
                newsSearchList.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = NewsLoadStateAdapter { adapter.retry() },
                    footer = NewsLoadStateAdapter { adapter.retry() }
                )
            }

            swipeRefresh.setOnRefreshListener {
                adapter.retry()
                swipeRefresh.isRefreshing = false
            }
            retryButton.setOnClickListener {
                adapter.retry()
            }

        }

        // Safely Collect the flow only when lifecycle is in at-least Started State

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchNewsFlow.collectLatest { value: PagingData<Article> ->
                adapter.submitData(value)
            }
        }


        adapter.addLoadStateListener { combinedLoadStates ->
            binding.apply {
                progressBar.isVisible = combinedLoadStates.source.refresh is LoadState.Loading
                newsSearchList.isVisible = combinedLoadStates.source.refresh is LoadState.NotLoading
                retryButton.isVisible = combinedLoadStates.source.refresh is LoadState.Error
                freshSearchTextView.isVisible = combinedLoadStates.source.refresh is LoadState.Error
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.saveArticlesFlow.collectLatest { event ->
                when (event) {
                    is SaveArticlesInfo.ArticlesSaveEvent -> {
                        viewModel.onFavouriteClicked(article = event.article)
                        Snackbar.make(requireView(), "Added to your Saved Articles", LENGTH_LONG)
                            .setBackgroundTint(resources.getColor(R.color.purple_700))
                            .setTextColor(resources.getColor(R.color.white))
                            .setAnchorView(R.id.bottom_nav)
                            .show()
                    }
                    else ->
                        return@collectLatest
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_item, menu)
        val searchItem = menu.findItem(R.id.action_search)

        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                viewModel.searchNews(query.toString())
                binding.newsSearchList.scrollToPosition(0)

                searchView.clearFocus()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchNews(newText.toString())
                if (newText != null) {
                    binding.freshSearchTextView.isVisible = false
                    binding.retryButton.isVisible = false
                }
                return true
            }

        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.relevance -> {
                viewModel.newsSortedByUser(SortParams().relevancy)
                binding.newsSearchList.scrollToPosition(0)
                true
            }

            R.id.popularity -> {
                viewModel.newsSortedByUser(SortParams().popularity)
                binding.newsSearchList.scrollToPosition(0)
                true
            }
            R.id.recent -> {
                viewModel.newsSortedByUser(SortParams().publishedAt)
                binding.newsSearchList.scrollToPosition(0)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}