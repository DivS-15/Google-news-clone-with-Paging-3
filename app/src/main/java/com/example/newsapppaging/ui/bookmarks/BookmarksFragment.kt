package com.example.newsapppaging.ui.bookmarks

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.newsapppaging.R
import com.example.newsapppaging.databinding.BookmarksBinding
import com.example.newsapppaging.ui.NewsViewModel
import com.example.newsapppaging.ui.headlines.SaveArticlesInfo
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarksFragment : Fragment(R.layout.bookmarks) {

    private var _binding: BookmarksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = BookmarksBinding.bind(view)
        val adapter = BookMarksAdapter(viewLifecycleOwner.lifecycleScope)

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)


        binding.apply {
            newsSearchList.apply {
                itemAnimator = null
                setHasFixedSize(true)
                this.adapter = adapter
                this.addItemDecoration(dividerItemDecoration)
            }

            alphaPrefSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
                preferencesAlphabetic.isVisible = compoundButton.isChecked
            }
        }



        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.savedArticlesSharedFlow.collectLatest {
                    adapter.submitList(it)
                }
            }
        }
        setHasOptionsMenu(false)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.deleteArticleFlow.collect { event ->
                when (event) {
                    is SaveArticlesInfo.DeleteArticleEvent -> {
                        viewModel.onDeleteClicked(event.article)
                        Snackbar.make(requireView(), "Article Deleted ", LENGTH_INDEFINITE)
                            .setAction(" UNDO ") {
                                viewModel.onUndoClicked(event.article)
                            }.setActionTextColor(resources.getColor(R.color.white))
                            .setBackgroundTint(resources.getColor(R.color.firebrick_red))
                            .setAnchorView(R.id.bottom_nav).show()
                    }
                }
            }
        }
    }
/*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_item, menu)
        val searchItem = menu.findItem(R.id.action_search)

        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.newsSearchList.scrollToPosition(0)
                    viewModel.searchNews(query.toString())
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}