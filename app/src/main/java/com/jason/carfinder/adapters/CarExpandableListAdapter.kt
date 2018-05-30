package com.jason.carfinder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.jason.carfinder.R
import com.jason.carfinder.models.Company

class CarExpandableListAdapter(private val context: Context, val companies: ArrayList<Company>) : BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosititon: Int) = companies[groupPosition].cars[childPosititon]

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun getChildView(position: Int, childPosition: Int,
                              isLastChild: Boolean, convertView: View?, parent: ViewGroup): View? {
        var cv = convertView
        if (convertView == null) {
            cv = LayoutInflater.from(context).inflate(R.layout.car_item, null)
        }

        val type = cv?.findViewById<TextView>(R.id.category_type)
        val price = cv?.findViewById<TextView>(R.id.price)
        val code = cv?.findViewById<TextView>(R.id.acriss_code)
        val fuel = cv?.findViewById<TextView>(R.id.fuel_type)
        val air = cv?.findViewById<TextView>(R.id.air_conditioning)

        type?.let {
            it.text = String.format(context.getString(R.string.category_type),
                    getChild(position, childPosition).vehicle_info.category,
                    getChild(position, childPosition).vehicle_info.type)
        }
        price?.let {
            it.text = String.format(context.getString(R.string.price_formatted),
                    getChild(position, childPosition).estimated_total.amount)
        }
        code?.let { it.text = getChild(position, childPosition).vehicle_info.acriss_code }
        fuel?.let {
            it.text = String.format(context.getString(R.string.fuel_type),
                    getChild(position, childPosition).vehicle_info.fuel,
                    getChild(position, childPosition).vehicle_info.type)
        }
        air?.let {
            it.text = String.format(context.getString(R.string.air_conditioning),
                    getChild(position, childPosition).vehicle_info.air_conditioning)
        }
        return cv
    }

    override fun getChildrenCount(groupPosition: Int) = companies[groupPosition].cars.size

    override fun getGroup(groupPosition: Int) = companies[groupPosition]

    override fun getGroupCount() = companies.size

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getGroupView(position: Int, isExpanded: Boolean,
                              convertView: View?, parent: ViewGroup): View? {
        var cv = convertView
        if (convertView == null) {
            cv = LayoutInflater.from(context).inflate(R.layout.company_header, null)
        }
        val name = cv?.findViewById<TextView>(R.id.company_name)
        val addressLine1 = cv?.findViewById<TextView>(R.id.address_line_1)
        val addressLine2 = cv?.findViewById<TextView>(R.id.address_line_2)

        name?.let { it.text = getGroup(position).provider.company_name }
        addressLine1?.let { it.text = getGroup(position).address.line1 }
        addressLine2?.let {
            it.text = String.format(
                    context.getString(R.string.address_line_2),
                    getGroup(position).address.city,
                    getGroup(position).address.region,
                    getGroup(position).address.country)
        }

        return cv
    }

    override fun hasStableIds() = false

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true
}