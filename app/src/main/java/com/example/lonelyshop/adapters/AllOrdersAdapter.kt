package com.example.lonelyshop.adapters

import Order
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.lonelyshop.R
import com.example.lonelyshop.data.order.OrderStatus
import com.example.lonelyshop.data.order.getOrders
import com.example.lonelyshop.databinding.OrderItemBinding

class AllOrdersAdapter : RecyclerView.Adapter<AllOrdersAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(private val binding: OrderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.date

                val color = when (getOrders(order.orderStatus)) {
                    is OrderStatus.Ordered -> R.color.g_orange_yellow
                    is OrderStatus.Confirmed -> R.color.g_green
                    is OrderStatus.Delivered -> R.color.g_green
                    is OrderStatus.Shipped -> R.color.g_green
                    is OrderStatus.Cancelled -> R.color.g_red
                    is OrderStatus.Returned -> R.color.g_red
                }
                Log.d("AllOrdersAdapter", "Order Status: ${order.orderStatus}, Color: $color")

                // Set the color to the ImageView
                imageOrderState.setColorFilter(ContextCompat.getColor(itemView.context, color))
            }
        }
    }


    private val diffUtil = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId // Compare based on a unique identifier
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem // Compare based on the content
        }
    }


    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
       return OrdersViewHolder(OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onClick: ((Order) -> Unit)? = null
}
