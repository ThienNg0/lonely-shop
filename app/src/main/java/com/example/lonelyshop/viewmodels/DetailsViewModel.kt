package com.example.lonelyshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lonelyshop.data.CartProduct
import com.example.lonelyshop.firebase.FirebaseComom
import com.example.lonelyshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    var auth: FirebaseAuth,
    private val firebaseComom: FirebaseComom
): ViewModel() {
    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())

    val addToCart = _addToCart
            fun addUpdateProductInCart(cartProduct: CartProduct){
                viewModelScope.launch {
                    _addToCart.emit(Resource.Loading())
                }
                firestore.collection("user").document(auth.uid!!).
                collection("cart")
                    .whereEqualTo("product.id", cartProduct.product.id)
                    .get().addOnSuccessListener {
                        it.documents.let {
                            if (it.isEmpty()){
                                    addNeweProduct(cartProduct)
                            }else
                            {
                                val product = it.first().toObject(CartProduct::class.java)
                                if (product == cartProduct){
                                    val documentId = it.first().id
                                    incressQuantity(documentId,cartProduct)

                                }else{
                                    addNeweProduct(cartProduct)
                                }
                            }
                        }
                }.addOnFailureListener {
                        viewModelScope.launch {
                            _addToCart.emit(Resource.Error(it.message.toString()))
                        }
                    }
            }
    private fun addNeweProduct(cartProduct: CartProduct){
        firebaseComom.addProductToCart(cartProduct){addedProduct, e->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(addedProduct!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }

        }
    }
    private fun incressQuantity(documentId: String, cartProduct: CartProduct){
        firebaseComom.decreaseQuanlity(documentId){ addedProduct, e ->
                viewModelScope.launch {
                    if (e == null)
                        _addToCart.emit(Resource.Success(cartProduct!!))
                    else
                        _addToCart.emit(Resource.Error(e.message.toString()))
                }
        }

    }

}