package com.vaveylax.yeniappwkotlin.ui.activity.splashScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.vaveylax.yeniappwkotlin.R

class IntroSliderAdapter(
    fm: FragmentManager
) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 4
    }

    override fun getItem(i: Int): Fragment {
        when (i) {
            0 -> {
                return WalkThroughOne()
            }
            1 -> {
                return WalkThroughTwo()
            }
            2 -> {
                return WalkThroughThree()
            }
            else -> {
                return WalkThroughFour()
            }
        }
    }
    class WalkThroughOne : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.walk_through_one, container, false)
        }
    }

    class WalkThroughTwo : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.walk_through_two, container, false)
        }
    }

    class WalkThroughThree : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.walk_through_three, container, false)
        }
    }

    class WalkThroughFour : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.walk_through_four, container, false)
        }
    }
}