package com.example.lonelyshop.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lonelyshop.R
import com.example.lonelyshop.adapters.ColorsAdapter
import com.example.lonelyshop.adapters.SizesAdapter
import com.example.lonelyshop.adapters.ViewPager2Images
import com.example.lonelyshop.data.CartProduct
import com.example.lonelyshop.databinding.FragmentProductDetailsBinding
import com.example.lonelyshop.util.Resource
import com.example.lonelyshop.util.hideBottomNavigation
import com.example.lonelyshop.viewmodels.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment: Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
        private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy {SizesAdapter()  }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor:Int? = null
    private var selectedSize:String? = null
    private val viewModel by viewModels<DetailsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         hideBottomNavigation()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupViewpager()
        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }
        sizesAdapter.onItemClickListener= { selectedSize = it }
        colorsAdapter.onItemClickListener= { selectedColor = it}

        binding.buttonAddtoCart.setOnClickListener {
            if (selectedColor == null) {
                Toast.makeText(requireContext(), "Please select a color.", Toast.LENGTH_SHORT).show()
            } else if (selectedSize == null) {
                Toast.makeText(requireContext(), "Please select a size.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize))
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.buttonAddtoCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonAddtoCart.revertAnimation()
                        binding.buttonAddtoCart.setBackgroundColor(resources.getColor(R.color.black))
                    }
                    is Resource.Error -> {
                        binding.buttonAddtoCart.stopAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit


                }
            }
        }



        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "$ ${product.price}"
            tvProductDescription.text = product.description
            if(product.colors.isNullOrEmpty())
            {
                tvProductColor.visibility = View.INVISIBLE
            }
            if(product.sizes.isNullOrEmpty())
            {
                tvProductSize.visibility = View.INVISIBLE
            }
        }
        viewPagerAdapter.differ.submitList(product.images)
       product.colors?.let {
           Log.d("ProductDetailsFragment", "Sizes: $it")
           colorsAdapter.differ.submitList(it)
       }
        product.sizes?.let {
            Log.d("ProductDetailsFragment", "Colors: $it")
            sizesAdapter.differ.submitList(it)
        }


    }

    private fun setupSizesRv() {
        binding.rvProductSize.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupColorsRv() {
      binding.rvProductColor.apply {
          adapter = colorsAdapter
          layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
      }


    }

    private fun setupViewpager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter

        }
    }


}