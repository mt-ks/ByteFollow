package com.kubernet.bytefollow.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kubernet.bytefollow.R
import com.kubernet.bytefollow.model.MyOrderDetail


class MyOrdersAdapter(private val context: Context,
                      private val data : List<MyOrderDetail>
                      ) : RecyclerView.Adapter<MyOrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.rc_my_order_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = data[position]
        Glide.with(context).load(order.display_url).into(holder.displayImage)
        holder.linkField.text =  order.success
        when(order.is_published)
        {
            0 -> holder.orderStatus.text = context.getString(R.string.order_completed)
            1 -> holder.orderStatus.text = context.getString(R.string.order_in_progress)
            2 -> holder.orderStatus.text = context.getString(R.string.order_canceled)
        }
        holder.parent.setOnClickListener {
            try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(order.link))) }catch (e : Exception){}
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val displayImage : ImageView = itemView.findViewById(R.id.displayImage)
        val linkField : TextView = itemView.findViewById(R.id.linkField)
        val orderStatus : TextView = itemView.findViewById(R.id.orderStatus)
        val parent : LinearLayout = itemView.findViewById(R.id.linkRoute)
    }

}