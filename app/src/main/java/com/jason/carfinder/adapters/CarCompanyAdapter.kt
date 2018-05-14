package com.jason.carfinder.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jason.carfinder.BR
import com.jason.carfinder.R
import com.jason.carfinder.databinding.CompanyRecyclerItemBinding
import com.jason.carfinder.models.Company

class CarCompanyAdapter(private val results: ArrayList<Company>) : RecyclerView.Adapter<CarCompanyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.company_recycler_item, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
            = holder.bindItem(results[position])

    override fun getItemCount() = results.size

    inner class ViewHolder(val binding: CompanyRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(company: Company) {
//            binding.company = company
            binding.setVariable(BR.company, company)
            binding.executePendingBindings()
        }
    }

}
