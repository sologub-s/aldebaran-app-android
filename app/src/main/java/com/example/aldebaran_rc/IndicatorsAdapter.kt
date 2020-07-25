package com.example.aldebaran_rc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.collection.ArrayMap
import kotlinx.android.synthetic.main.indicator_item.view.*

class IndicatorsAdapter(var context: Context?, foodsList: ArrayMap<String, MainIndicator>) :

    BaseAdapter() {
    var indicatorsList = foodsList

    override fun getCount(): Int {
        return indicatorsList.size
    }

    override fun getItem(position: Int): MainIndicator? {

        var i: Int = 0;

        for ((_, v) in indicatorsList) {
            if (i == position) {
                return v
            }
            i++
        }

        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val indicator = this.getItem(position)

        val inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val indicatorsView = inflator.inflate(R.layout.indicator_item, null)
        indicatorsView.imgIndicator.setImageResource(indicator?.image!!)
        indicatorsView.indicatorName.text = indicator.name!!

        return indicatorsView
    }
}