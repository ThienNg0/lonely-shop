package com.example.lonelyshop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lonelyshop.data.Product
import com.example.lonelyshop.databinding.BestDealsRvItemBinding

class BestDealsAdapter: RecyclerView.Adapter<BestDealsAdapter.BeastDealsViewHolder>() {

    inner class BeastDealsViewHolder(private val binding : BestDealsRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(products: Product) {
            binding.apply {
                // Sử dụng Glide để tải hình ảnh đầu tiên trong danh sách hình ảnh của sản phẩm vào ImageView imgBestDeal.
                Glide.with(itemView).load(products.images[0]).into(imgBestDeal)

                // Kiểm tra xem sản phẩm có giá giảm giá (offerPercentage) không.
                products.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * products.price
                    tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                }
                tvOldPrice.text = "$ ${products.price}"
                tvDealProductName.text = products.name
            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,diffCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeastDealsViewHolder {
        return BeastDealsViewHolder(
            BestDealsRvItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: BeastDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bindData(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onClick : ((Product) -> Unit)? = null
}