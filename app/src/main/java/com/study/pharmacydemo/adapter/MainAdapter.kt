package com.study.pharmacydemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.study.pharmacydemo.data.Feature
import com.study.pharmacydemo.databinding.ItemViewBinding

class MainAdapter(private val iItemClickListener: IItemClickListener) :
    RecyclerView.Adapter<MainAdapter.MyViewHolder>() {

    var pharmacyList: List<Feature> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class MyViewHolder(val itemViewBinding: ItemViewBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemViewBinding =
            ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemViewBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var property = pharmacyList[position].property
        holder.itemViewBinding.apply {
            tvName.text = property.name
            tvAdultAmount.text = property.mask_adult.toString()
            tvChildAmount.text = property.mask_child.toString()
            clItem.setOnClickListener {
                iItemClickListener.onItemClickListener(pharmacyList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return pharmacyList.size
    }

    interface IItemClickListener {
        fun onItemClickListener(data: Feature)
    }

}
