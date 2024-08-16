package com.example.lonelyshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lonelyshop.data.Category
import com.example.lonelyshop.data.Product
import com.example.lonelyshop.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModel() {

    // StateFlow để lưu trữ trạng thái của sản phẩm khuyến mãi
    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    // StateFlow để lưu trữ trạng thái của sản phẩm tốt nhất
    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()
    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    // Hàm để lấy danh sách sản phẩm khuyến mãi
    fun fetchOfferProducts() {
        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", category.category) // Sửa lại để lấy giá trị của category từ thuộc tính name
            .whereNotEqualTo("offerPercentage", null)
            .get()
            .addOnSuccessListener { result ->
                val products = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products)) // Cập nhật trạng thái là "Success" với danh sách sản phẩm khuyến mãi
                }

            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }

            }
    }
    fun fetchBestProducts() {
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", category.category) // Sửa lại để lấy giá trị của category từ thuộc tính name
            .whereEqualTo("offerPercentage", null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(products)) // Cập nhật trạng thái là "Success" với danh sách sản phẩm khuyến mãi
                }

            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }

            }
    }
}
