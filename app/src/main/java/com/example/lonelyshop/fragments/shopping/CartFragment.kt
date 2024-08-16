package com.example.lonelyshop.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lonelyshop.R
import com.example.lonelyshop.adapters.CartProductsAdapter
import com.example.lonelyshop.databinding.FragmentCartBinding
import com.example.lonelyshop.firebase.FirebaseComom
import com.example.lonelyshop.util.Resource
import com.example.lonelyshop.util.VerticalItemDecoration
import com.example.lonelyshop.viewmodels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy {
        CartProductsAdapter()
    }
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartRv()
        var totalPrice = 0f
        lifecycleScope.launchWhenStarted {
            viewModel.productPrice.collectLatest { price ->
                price?.let {
                    totalPrice = it
                    binding.tvTotalPrice.text = "$ $price"
                }
            }
        }


        cartAdapter.onProductClick = {
            val b = Bundle().apply {
                putParcelable("product", it.product)
            }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment2, b)
        }
        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseComom.QuantityChanging.INCREASE)
        }
        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseComom.QuantityChanging.DECREASE)
        }
        binding.buttonCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(
                totalPrice, cartAdapter.differ.currentList.toTypedArray(),true
            )
            findNavController().navigate(action)
        }

        lifecycleScope.launch {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = android.app.AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart?")
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes") { dialog, _ ->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it)
                {
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if(it.data!!.isEmpty())
                        {
                            showEmptyCart()
                            hideOtherViews()

                        }else{
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)

                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }



            }
        }
    }

    private fun showOtherViews() {
       binding.apply {
           rvcart.visibility = View.VISIBLE
           totalBoxContainer.visibility = View.VISIBLE
           buttonCheckout.visibility = View.VISIBLE
       }
    }

    private fun hideOtherViews() {
       binding.apply {
           rvcart.visibility = View.GONE
           totalBoxContainer.visibility = View.GONE
           buttonCheckout.visibility = View.GONE
       }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmty.visibility = View.VISIBLE
        }
    }

    private fun setupCartRv() {
       binding.rvcart.apply {
           layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
           adapter = cartAdapter
           addItemDecoration(VerticalItemDecoration())

       }
    }


}
