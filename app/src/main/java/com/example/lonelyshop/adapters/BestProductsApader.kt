package com.example.lonelyshop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lonelyshop.data.Product
import com.example.lonelyshop.databinding.ProductRvItemBinding
import com.example.lonelyshop.helper.getProductPrice

class BestProductsAdapter: RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {
    inner class BestProductsViewHolder(private val binding: ProductRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(products: Product) {
            binding.apply {
                if (products.images.isNotEmpty()) {
                    Glide.with(itemView).load(products.images[0]).into(imgProduct)
                } else {
                    // Xử lý trường hợp không có hình ảnh, ví dụ: ẩn ImageView
                    imgProduct.setImageDrawable(null) // Hoặc ẩn ImageView
                }

                products.offerPercentage?.let {

                    val priceAfterOffer = products.offerPercentage.getProductPrice(products.price)
                    tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                }
                if (products.offerPercentage == null)
                    tvNewPrice.visibility = View.INVISIBLE
                tvPrice.text = "$ ${products.price}"
                tvName.text = products.name
            }
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        val binding = ProductRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BestProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bindData(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onClick: ((Product) -> Unit)? = null
}
