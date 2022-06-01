package com.example.newsapppaging.ui.bookmarks

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.newsapppaging.data.Article
import com.example.newsapppaging.databinding.SearchListItemBinding
import com.example.newsapppaging.ui.headlines.SaveArticlesInfo
import com.example.newsapppaging.utils.getSimpleDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


class BookMarksAdapter(private val scope: CoroutineScope) :
    ListAdapter<Article, BookMarksAdapter.BookMarksViewHolder>(DiffCallback) {

    private val deleteArticleChannel = Channel<SaveArticlesInfo>()
    val deleteArticleFlow = deleteArticleChannel.receiveAsFlow()

    companion object DiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    inner class BookMarksViewHolder(private val binding: SearchListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {

                root.setOnClickListener {
                    val article = getItem(absoluteAdapterPosition)
                    openNewsArticleInChrome(article.url, itemView.context)
                }
                /*
                    deleteArticleImage.isVisible = true
                    saveArticleHeartImage.isVisible = false
                    saveArticleHeartAnimation.isVisible = false

                    deleteArticleImage.setOnClickListener {
                        scope.launch {
                            val article = getItem(absoluteAdapterPosition)
                            deleteArticleChannel.send(SaveArticlesInfo.DeleteArticleEvent(article))
                        }

                    }*/
            }
        }

        fun bind(article: Article) {
            binding.apply {
                title.text = article.title
                sourceName.text = article.source.name
                timePublished.text = getSimpleDate(article.publishedAt.toString())


                deleteArticleImage.isVisible = true

                val imgUrl = article.urlToImage

                imgUrl?.let {
                    val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
                    newsImage.load(imgUri) {
                        transformations(RoundedCornersTransformation(25f))
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookMarksViewHolder {
        val binding =
            SearchListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookMarksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookMarksViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(it)
        }
    }

    private fun openNewsArticleInChrome(url: String, context: Context) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}