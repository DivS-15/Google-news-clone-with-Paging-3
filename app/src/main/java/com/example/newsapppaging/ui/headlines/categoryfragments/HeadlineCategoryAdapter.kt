package com.example.newsapppaging.ui.headlines.categoryfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newsapppaging.ui.headlines.TopHeadlinesFragment
import javax.inject.Inject

private const val TAG = "HeadlineCategoryAdapter"

class HeadlineCategoryAdapter @Inject constructor(fragment: Fragment) :

    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 9
    }

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = TopHeadlinesFragment()

        fragment.arguments = Bundle().apply {
            putInt("tab", position + 1)

        }
        Log.d(TAG, "fragment returned: $position")
        return fragment

    }
}