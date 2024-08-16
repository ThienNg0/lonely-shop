package com.example.lonelyshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lonelyshop.data.CartProduct
import com.example.lonelyshop.firebase.FirebaseComom
import com.example.lonelyshop.helper.getProductPrice
import com.example.lonelyshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseComom: FirebaseComom
):ViewModel() {

    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    val productPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }

            else -> null
        }
    }
    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog =  _deleteDialog.asSharedFlow()
    private var cartProductDocuments = emptyList<DocumentSnapshot>()


    fun deleteCartProduct(cartProduct: CartProduct)
    {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1)
        {
            val documentId = cartProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!)
                .collection("cart")
                .document(documentId).delete()
        }

    }


    private fun calculatePrice(data: List<CartProduct>): Float? {

        return data.sumByDouble { cartProducts ->
            (cartProducts.product.offerPercentage.getProductPrice(cartProducts.product.price) * cartProducts.quantity).toDouble()
        }.toFloat()
    }







    init {
        getCartProducts()
    }

    private fun getCartProducts() {
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
            firestore.collection("user").document(auth.uid!!).collection("cart")
                .addSnapshotListener { value, error ->
                    if (error != null || value == null) {
                        viewModelScope.launch {
                            _cartProducts.emit(Resource.Error(error?.message.toString()))
                        }
                    } else {
                        cartProductDocuments = value.documents
                        val cartProducts = value.toObjects(CartProduct::class.java)
                        viewModelScope.launch {
                            Resource.Success(_cartProducts.emit(Resource.Success(cartProducts)))
                        }
                    }
                }
        }
    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quanlityChanging: FirebaseComom.QuantityChanging
    ) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
//        index cloud be euqal to if -1
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index!!].id
            when (quanlityChanging) {
                FirebaseComom.QuantityChanging.INCREASE -> {
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    incressQuanlity(documentId)
                }

                FirebaseComom.QuantityChanging.DECREASE -> {
                    if(cartProduct.quantity == 1 && quanlityChanging == FirebaseComom.QuantityChanging.DECREASE)
                    {
                        viewModelScope.launch {
                            _deleteDialog.emit(cartProduct)
                        }
                        return
                    }
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    decressQuanlity(documentId)
                }
            }


        }

    }

    private fun decressQuanlity(documentId: String) {
        firebaseComom.decreaseQuanlity(documentId) { result, exception ->
            if (exception != null) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun incressQuanlity(documentId: String) {
        firebaseComom.incressQuanlity(documentId) { result, exception ->
            if (exception != null) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }
}


