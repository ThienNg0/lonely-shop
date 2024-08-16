package com.example.lonelyshop.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.View
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
class FurnitureFragment: BaseCategoryFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Furniture)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showOfferLoading()

                    }
                    is Resource.Success -> {

                        hideOfferLoading()
                        offerAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {

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

                    }
                    is Resource.Success -> {

                        hideBestProductsLoading()
                        bestProductsAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {

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