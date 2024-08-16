package com.example.lonelyshop.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lonelyshop.data.CartProduct

import com.example.lonelyshop.databinding.CartProductItemBinding
import com.example.lonelyshop.helper.getProductPrice
@Suppress("DEPRECATION")
class CartProductsAdapter: RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder>() {

    inner class CartProductsViewHolder( val binding: CartProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartproduct: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(cartproduct.product.images[0]).into(imageCartProduct)
               tvProductCartName.text = cartproduct.product.name
                tvProductCartQuantity.text = cartproduct.quantity.toString()

                    val priceAfterOffer = cartproduct.product.offerPercentage.getProductPrice(cartproduct.product.price)
                    tvProductCartPrice.text = "$ ${String.format("%.2f",priceAfterOffer)}"
                imageCartProductColor.setImageDrawable(ColorDrawable(cartproduct.slectedColor?: Color.TRANSPARENT))
                tvCartProductSize.text = cartproduct.selectedSize?:"".also { imageCartProductSize
                    .setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }

            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)
        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }
        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }
        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onProductClick: ((CartProduct) -> Unit)? = null
    var onPlusClick: ((CartProduct) -> Unit)? = null
    var onMinusClick: ((CartProduct) -> Unit)? = null
}
