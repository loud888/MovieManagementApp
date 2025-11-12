package com.example.moviemanagement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.moviemanagement.ui.genre.GenreFragment
import com.example.moviemanagement.ui.movie.MovieFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab)
            .setOnClickListener {
                com.example.moviemanagement.ui.movie.AddMovieDialog().show(supportFragmentManager, "add_movie")
            }

        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(GenreFragment(), "Thể loại")
        adapter.addFragment(MovieFragment(), "Phim")
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }
}

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private val fragments = ArrayList<Fragment>()
    private val titles = ArrayList<String>()

    fun addFragment(f: Fragment, title: String) {
        fragments.add(f)
        titles.add(title)
    }

    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position]
    fun getPageTitle(position: Int) = titles[position]
}
