package com.example.newsapppaging.ui.headlines.categoryfragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.newsapppaging.R
import com.example.newsapppaging.databinding.DemoContainerFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HeadlinesCollectionFragment : Fragment(R.layout.demo_container_fragment) {

    @Inject
    lateinit var collectionAdapter: HeadlineCategoryAdapter

    private lateinit var viewPager2: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = DemoContainerFragmentBinding.bind(view)

        collectionAdapter = HeadlineCategoryAdapter(this)

        viewPager2 = binding.pager
        viewPager2.adapter = collectionAdapter

        TabLayoutMediator(binding.tabLayout, viewPager2) { tab, position ->

            if (position == 0) {
                tab.text = "World"
            }

            if (position == 1) {
                tab.text = "India"
            }
            if (position == 2) {
                tab.text = "Business"
            }
            if (position == 3) {
                tab.text = "Entertainment"
            }
            if (position == 4) {
                tab.text = "General"
            }
            if (position == 5) {
                tab.text = "Health"
            }
            if (position == 6) {
                tab.text = "Science"
            }

            if (position == 7) {
                tab.text = "Sports"
            }
            if (position == 8) {
                tab.text = "Technology"
            }


        }.attach()

    }
}