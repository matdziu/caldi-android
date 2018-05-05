package com.caldi.filterpeople.spinner

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class FilterSpinnerAdapter(context: Context, @LayoutRes resource: Int)
    : ArrayAdapter<FilterType>(context, resource) {

    private val filterTypeList = arrayListOf<FilterType>()

    override fun getItem(position: Int): FilterType = filterTypeList[position]

    override fun getCount(): Int = filterTypeList.size

    fun setFilterTypeList(filterTypeList: List<FilterType>) {
        this.filterTypeList.clear()
        this.filterTypeList.addAll(filterTypeList)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return setLabelTextView(super.getView(position, convertView, parent) as TextView, position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return setLabelTextView(super.getDropDownView(position, convertView, parent) as TextView, position)
    }

    private fun setLabelTextView(labelTextView: TextView, position: Int): TextView {
        labelTextView.text = filterTypeList[position].text
        return labelTextView
    }
}