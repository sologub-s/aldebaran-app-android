package com.example.aldebaran_rc.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import com.example.aldebaran_rc.*
import com.example.aldebaran_rc.enums.IntentActions
import kotlinx.android.synthetic.main.fragment_first.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@SuppressLint("ClickableViewAccessibility")
class SecondFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
        //return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        log("SecondFragment : onViewCreated")

    }

    override fun onDestroyView() {

        log("FirstFragment : onDestroyView")

        super.onDestroyView()
    }

    companion object {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(): SecondFragment {
            return SecondFragment()
        }
    }
}
