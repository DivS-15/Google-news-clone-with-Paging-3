package com.example.newsapppaging.ui.loadstate_list_item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapppaging.R
import com.example.newsapppaging.databinding.LoadstateItemBinding

class LoadStateViewHolder(
    private val binding: LoadstateItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(
    binding.root
) {
    init {
        binding.loadRetryButton.setOnClickListener {
            retry.invoke()
        }
    }

    fun bind(loadState: LoadState) {
        binding.apply {
            loadProgressBar.isVisible = loadState is LoadState.Loading
            retryMessage.isVisible = loadState is LoadState.Error
            loadRetryButton.isVisible = loadState is LoadState.Error
            endOfNewsItemsText.isVisible = loadState.endOfPaginationReached && loadState is LoadState.NotLoading
        }
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): LoadStateViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.loadstate_item, parent, false)

            val binding = LoadstateItemBinding.bind(view)
            return LoadStateViewHolder(binding, retry)
        }
    }

}