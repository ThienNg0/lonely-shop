package com.example.lonelyshop.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.lonelyshop.databinding.SizeRvItemBinding

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() {

    private var selectedPosition = -1

    inner class SizesViewHolder(private val binding: SizeRvItemBinding) : ViewHolder(binding.root) {

        fun bind(size: String, position: Int) {
            binding.tvSize.text = size

            if (position == selectedPosition) {
                binding.apply {
                    imageShadow.visibility = android.view.View.VISIBLE
                }
            } else {
                binding.apply {
                    imageShadow.visibility = android.view.View.INVISIBLE
                }
            }
        }
    }


    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    // Sử dụng AsyncListDiffer để tự động quản lý các thay đổi trong danh sách
   val differ = AsyncListDiffer(this, diffCallback)

    // Tạo ViewHolder mới khi cần thiết
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder(
            SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    // Liên kết dữ liệu với ViewHolder
    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size, position)

        // Xử lý sự kiện khi người dùng nhấp vào một item
        holder.itemView.setOnClickListener {
            Log.d("SizesAdapter", "Item clicked at position $position")
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClickListener?.invoke(size)
        }
    }

    // Trả về số lượng item trong danh sách
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Callback để xử lý sự kiện khi một item được nhấp
    var onItemClickListener: ((String) -> Unit)? = null
}
