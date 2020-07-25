package com.example.aldebaran_rc.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.aldebaran_rc.R
import com.example.aldebaran_rc.main.FirstFragment

private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2
)

private val FRAGMENTS = arrayOf(
    FirstFragment::class
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {

        val a = 1

        if (position == 0) {
            return FirstFragment.newInstance()
        }

        if (position == 1) {
            return SecondFragment.newInstance()
            //return PlaceholderFragment.newInstance(position)
        }

        throw Throwable()
        /*
        val constructor = FRAGMENTS[position].constructors.minBy { 0 } ?: throw Throwable()
        return constructor.call()
        */

        //return PlaceholderFragment.newInstance(position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}