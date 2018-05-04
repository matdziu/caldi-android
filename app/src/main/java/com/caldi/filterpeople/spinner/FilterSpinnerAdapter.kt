package com.caldi.filterpeople.spinner

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.caldi.filterpeople.models.spinner.FilterSpinnerItem

class FilterSpinnerAdapter(context: Context, @LayoutRes resource: Int)
    : ArrayAdapter<FilterSpinnerItem>(context, resource) {

    private val filterSpinnerItemList = arrayListOf<FilterSpinnerItem>()

    override fun getItem(position: Int): FilterSpinnerItem = filterSpinnerItemList[position]

    override fun getCount(): Int = filterSpinnerItemList.size

    fun setFilterSpinnerItemList(filterSpinnerItemList: List<FilterSpinnerItem>) {
        this.filterSpinnerItemList.clear()
        this.filterSpinnerItemList.addAll(filterSpinnerItemList)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return setLabelTextView(super.getView(position, convertView, parent) as TextView, position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return setLabelTextView(super.getDropDownView(position, convertView, parent) as TextView, position)
    }

    private fun setLabelTextView(labelTextView: TextView, position: Int): TextView {
        labelTextView.text = filterSpinnerItemList[position].text
        return labelTextView
    }
}