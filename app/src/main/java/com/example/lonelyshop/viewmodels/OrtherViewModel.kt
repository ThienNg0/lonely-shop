package com.example.lonelyshop.viewmodels

import Order
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lonelyshop.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    fun placeOrder(order: Order) {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
            try {
                val batch = firestore.batch()

                // Add the order to user-orders collection
                val userOrderRef = firestore.collection("user")
                    .document(auth.uid!!)
                    .collection("orders")
                    .document()
                batch.set(userOrderRef, order)

                // Add the order to orders collection
                val orderRef = firestore.collection("orders").document()
                batch.set(orderRef, order)

                // Delete the products from user-cart collection
                val cartCollection = firestore.collection("user")
                    .document(auth.uid!!)
                    .collection("cart")
                val cartSnapshot = cartCollection.get().await()
                cartSnapshot.documents.forEach {
                    batch.delete(it.reference)
                }

                // Commit the batch
                batch.commit().await()

                _order.emit(Resource.Success(order))
            } catch (e: Exception) {
                _order.emit(Resource.Error(e.message.toString()))
            }
        }
    }
}
