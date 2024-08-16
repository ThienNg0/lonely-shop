package com.example.lonelyshop.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.lonelyshop.data.Category
import com.example.lonelyshop.util.Resource
import com.example.lonelyshop.viewmodels.CategoryViewModel
import com.example.lonelyshop.viewmodels.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
@AndroidEntryPoint
class ChairFragment: BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore,Category.Chair)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showOfferLoading()
                        Log.d("ChairFragment", "Loading offer products")
                    }
                    is Resource.Success -> {
                        Log.d("ChairFragment", "Offer products received: ${it.data}")
                        hideOfferLoading()
                        offerAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        Log.e("ChairFragment", "Error: ${it.message}")
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {

            viewModel.bestProducts.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showBestProductsLoading()
                        Log.d("ChairFragment", "Loading best products")
                    }
                    is Resource.Success -> {
                        Log.d("ChairFragment", "Best products received: ${it.data}")
                        hideBestProductsLoading()
                        bestProductsAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        Log.e("ChairFragment", "Error: ${it.message}")
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onBestProductsPagingRequest() {
        super.onBestProductsPagingRequest()
    }

    override fun onOfferPagingRequest() {
        super.onOfferPagingRequest()
    }
}