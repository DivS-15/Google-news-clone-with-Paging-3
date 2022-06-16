package com.example.newsapppaging.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.newsapppaging.data.model.Article
import com.example.newsapppaging.databinding.SearchListItemBinding
import com.example.newsapppaging.ui.headlines.SaveArticlesInfo
import com.example.newsapppaging.utils.getDateTimeDifference
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class NewsAdapter :
    PagingDataAdapter<Article, NewsAdapter.NewsViewHolder>(DiffCallback) {

    private val saveArticlesChannel = Channel<SaveArticlesInfo>()
    val saveArticlesFlow = saveArticlesChannel.receiveAsFlow()


    companion object DiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }


    }

    inner class NewsViewHolder(private val binding: SearchListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //inner class means we can access parent class' methods and properties like
        init {// getItem

            binding.apply {
                root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    val newsItem = getItem(position)
                    if (newsItem != null) {
                        openNewsArticleInChrome(newsItem.url, itemView.context)
                    }
                }


            }
        }

        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(article: Article) {
            binding.apply {

                val date = getDateTimeDifference(article.publishedAt.toString())

                title.text = article.title
                sourceName.text = article.source.name

                if (date.days.toInt() == 0) {
                    timePublished.text = "${date.hours} hours ago"
                }

                if (date.hours.toInt() == 0) {
                    timePublished.text = "${date.minutes} minutes ago"
                } else if (date.minutes.toInt() == 0) {
                    timePublished.text = "${date.seconds} seconds ago"
                }

                saveArticleImg.isVisible = true

                val imgUrl = article.urlToImage

                imgUrl?.let {
                    val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
                    newsImage.load(imgUri) {
                        transformations(RoundedCornersTransformation(20f))
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding =
            SearchListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    private fun openNewsArticleInChrome(url: String, context: Context) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

}

