package com.example.newsapppaging.ui.headlines

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
import com.example.newsapppaging.databinding.ListItemBinding
import com.example.newsapppaging.utils.getDateTimeDifference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class TopHeadLinesAdapter @Inject constructor(private val scope: CoroutineScope) :
    PagingDataAdapter<Article, TopHeadLinesAdapter.HeadlinesViewHolder>(DiffCallback) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlinesViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeadlinesViewHolder(binding)

    }


    inner class HeadlinesViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //inner class means we can access parent class' methods and properties like
        init {// getItem

            binding.saveArticleHeartAnimation.isVisible = false
            binding.apply {
                root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    val newsItem = getItem(position)
                    if (newsItem != null) {
                        openNewsArticleInChrome(newsItem.url, itemView.context)
                    }
                }

                saveArticleHeartImage.setOnClickListener {
                    saveArticleHeartImage.isVisible = false
                    saveArticleHeartAnimation.isVisible = true
                    saveArticleHeartAnimation.playAnimation()
                    val position = absoluteAdapterPosition
                    val newsItem = getItem(position)

                    newsItem?.let {
                        scope.launch {
                            saveArticlesChannel.send(SaveArticlesInfo.ArticlesSaveEvent(newsItem))
                        }

                    }

                }
            }
        }


        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(article: Article) {
            binding.apply {
                newsTitle.text = article.title
                source.text = article.source.name

                val date = getDateTimeDifference(article.publishedAt.toString())

                if (date.days.toInt() == 0) {
                    timePublished.text = "${date.hours} hours ago"
                }

                if (date.hours.toInt() == 0) {
                    timePublished.text = "${date.minutes} minutes ago"
                } else if (date.minutes.toInt() == 0) {
                    timePublished.text = "${date.seconds} seconds ago"
                }


                deleteArticleImage.isVisible = false

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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HeadlinesViewHolder, position: Int) {
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

sealed class SaveArticlesInfo() {
    data class ArticlesSaveEvent(val article: Article) : SaveArticlesInfo()
    data class DeleteArticleEvent(val article: Article) : SaveArticlesInfo()
    data class UndoDeleteEvent(val article: Article) : SaveArticlesInfo()
}