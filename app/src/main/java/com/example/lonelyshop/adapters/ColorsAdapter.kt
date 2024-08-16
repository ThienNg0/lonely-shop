package com.example.lonelyshop.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.RecyclerView
import com.example.lonelyshop.databinding.ColorRvItemBinding

class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {


    private var selectedPosition = -1


    inner class ColorsViewHolder(private val binding: ColorRvItemBinding) : ViewHolder(binding.root) {

        fun bind(color: Int, position: Int) {

            val imageDrawable = ColorDrawable(color)
            binding.imageColor.setImageDrawable(imageDrawable)

            if (position == selectedPosition) {
                binding.apply {
                    imageShadow.visibility = android.view.View.VISIBLE
                    imagePicked.visibility = android.view.View.VISIBLE
                }
            } else {
                binding.apply {
                    imageShadow.visibility = android.view.View.INVISIBLE
                    imagePicked.visibility = android.view.View.INVISIBLE
                }
            }
        }
    }


    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {

            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {

            return oldItem == newItem
        }
    }

    // Sử dụng AsyncListDiffer để tự động quản lý các thay đổi trong danh sách
    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder(
            ColorRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    // Liên kết dữ liệu với ViewHolder
    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color, position)


        holder.itemView.setOnClickListener {
            if(selectedPosition >= 0 )
                notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClickListener?.invoke(color)

        }
    }

    // Trả về số lượng item trong danh sách
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Callback để xử lý sự kiện khi một item được nhấp
    var onItemClickListener: ((Int) -> Unit)? = null
}
