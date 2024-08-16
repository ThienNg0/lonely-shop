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
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()

    init {
        getAllOrders()
    }

    fun getAllOrders() {
        _allOrders.value = Resource.Loading()

        firestore.collection("user")
            .document(auth.uid!!)
            .collection("orders")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    viewModelScope.launch {
                        _allOrders.emit(Resource.Error(e.message.toString()))
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val orders = snapshot.toObjects(Order::class.java)
                    viewModelScope.launch {
                        _allOrders.emit(Resource.Success(orders))
                    }
                } else {
                    viewModelScope.launch {
                        _allOrders.emit(Resource.Success(emptyList()))
                    }
                }
            }
    }
}
