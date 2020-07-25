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
class FirstFragment : Fragment() {

    private val crossButtons = listOf("button_dec_plus", "button_dec_minus", "button_ra_plus", "button_ra_minus")
    private var followEnabled = false
    private var laserEnabled = false
    private var gidEnabled = false

    private var adapter: IndicatorsAdapter? = null
    var indicatorsList = ArrayMap<String, MainIndicator>()

    private lateinit var broadcastReceiver : BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
        //return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        log("FirstFragment : onViewCreated")

        Intent(context, ControllerPullService::class.java).also { intent ->
            context?.startService(intent)
        }


        crossButtons.forEach {

            val button = view.findViewById<Button>(resources.getIdentifier(it, "id", activity?.packageName))

            button?.setOnTouchListener { _: View, motionEvent: MotionEvent ->
                when (motionEvent.action) {

                    // @TODO start slew here
                    MotionEvent.ACTION_DOWN -> {

                        when(button.tag) {
                            "button_dec_plus" -> {
                                indicatorsList["dec"] =  MainIndicator("DEC", R.drawable.ic_indicator_axis_plus_active)
                                gvMainIndicators.invalidateViews()
                            }
                            "button_dec_minus" -> {
                                indicatorsList["dec"] =  MainIndicator("DEC", R.drawable.ic_indicator_axis_minus_active)
                                gvMainIndicators.invalidateViews()
                            }
                            "button_ra_plus" -> {
                                indicatorsList["ra"] =  MainIndicator("RA", R.drawable.ic_indicator_axis_plus_active)
                                gvMainIndicators.invalidateViews()
                            }
                            "button_ra_minus" -> {
                                indicatorsList["ra"] =  MainIndicator("RA", R.drawable.ic_indicator_axis_minus_active)
                                gvMainIndicators.invalidateViews()
                            }
                        }
                    }

                    // @TODO stop slew here
                    MotionEvent.ACTION_UP -> {
                        when(button.tag) {
                            "button_dec_plus", "button_dec_minus" -> {
                                indicatorsList["dec"] =  MainIndicator("DEC", R.drawable.ic_indicator_compass)
                                gvMainIndicators.invalidateViews()
                            }
                            "button_ra_plus", "button_ra_minus" -> {
                                indicatorsList["ra"] =  MainIndicator("RA", R.drawable.ic_indicator_compass)
                                gvMainIndicators.invalidateViews()
                            }
                        }
                    }
                }
                true
            }

        }

        val buttonFollow = view.findViewById<Button>(R.id.button_follow)

        buttonFollow?.setOnClickListener {
            followEnabled = !followEnabled;
            indicatorsList["follow"] =  MainIndicator("FLW", if (followEnabled) R.drawable.ic_indicator_follow_active else R.drawable.ic_indicator_follow)
            gvMainIndicators.invalidateViews()
        }

        val buttonLaser = view.findViewById<Button>(R.id.button_laser)

        buttonLaser?.setOnClickListener {
            laserEnabled = !laserEnabled;
            indicatorsList["laser"] =  MainIndicator("LSR", if (laserEnabled) R.drawable.ic_indicator_laser_active else R.drawable.ic_indicator_laser)
            gvMainIndicators.invalidateViews()
        }

        // load indicators
        indicatorsList["follow"] =  MainIndicator("FLW", R.drawable.ic_indicator_follow)
        indicatorsList["dec"] =  MainIndicator("DEC", R.drawable.ic_indicator_compass)
        indicatorsList["ra"] =  MainIndicator("RA", R.drawable.ic_indicator_compass)
        indicatorsList["guiding"] =  MainIndicator("GID", R.drawable.ic_indicator_guiding)
        indicatorsList["laser"] =  MainIndicator("LSR", R.drawable.ic_indicator_laser)

        adapter = IndicatorsAdapter(activity?.applicationContext, indicatorsList)

        gvMainIndicators.adapter = adapter

        val intentFilter = IntentFilter()
        intentFilter.addAction(IntentActions.PULL_RECEIVED.name)
        // adding more filters...
        //intentFilter.addAction("received")
        //intentFilter.addAction("received")
        //intentFilter.addAction("received")
        //intentFilter.addAction("received")

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    it.action?.also {  action ->
                        when(action) {
                            IntentActions.PULL_RECEIVED.name -> {
                                log("FirstFragment : BroadcastReceiver : action : $action")

                                var needInvalidateView = false

                                if (intent.hasExtra("status_:_gid")) {
                                    val gidEnabledOld = gidEnabled
                                    gidEnabled = intent.getBooleanExtra("status_:_gid", gidEnabled);
                                    log("FirstFragment : BroadcastReceiver : action : $action : extra : status_:_gid : $gidEnabled")
                                    indicatorsList["guiding"] =  MainIndicator("GID", if (gidEnabled) R.drawable.ic_indicator_guiding_active else R.drawable.ic_indicator_guiding)
                                    if (gidEnabledOld != gidEnabled) {
                                        needInvalidateView = true
                                    }
                                }

                                if (needInvalidateView) {
                                    gvMainIndicators.invalidateViews()
                                }

                            }
                            "received2" -> {
                                log("FirstFragment : BroadcastReceiver : action : $action")
                            }
                            "received3" -> {
                                log("FirstFragment : BroadcastReceiver : action : $action")
                            }
                            else -> {
                                log("FirstFragment : BroadcastReceiver : action : unknown action : $action")
                            }
                        }
                    }
                }
            }
        }

        requireActivity().registerReceiver(broadcastReceiver, intentFilter)

        log("FirstFragment : onViewCreated : broadcastReceiver registered")
    }

    override fun onDestroyView() {

        log("FirstFragment : onDestroyView")

        requireActivity().unregisterReceiver(broadcastReceiver)

        log("FirstFragment : onDestroyView : broadcastReceiver unregistered")

        super.onDestroyView()
    }

    companion object {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(): FirstFragment {
            return FirstFragment()
        }
    }
}
