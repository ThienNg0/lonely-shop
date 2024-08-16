package com.example.lonelyshop.firebase

import com.example.lonelyshop.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseComom(
    private val firestor: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val cartCollection = firestor.collection("user").document(auth.currentUser?.uid ?: "").collection("cart")

    fun addProductToCart(cartproduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        cartCollection.document().set(cartproduct)
            .addOnSuccessListener {
                onResult(cartproduct, Exception("success"))
            }.addOnFailureListener {
                onResult(null, it)
            }


    }

    fun incressQuanlity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestor.runTransaction { transaction ->
            val docRef = cartCollection.document(documentId)
            val doc = transaction.get(docRef)
            val product = doc.toObject(CartProduct::class.java)
            product?.let {cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProduct = cartProduct.copy(quantity = newQuantity)
                transaction.set(docRef, newProduct)
            }
        }.addOnSuccessListener {
                onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }
    fun decreaseQuanlity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestor.runTransaction { transaction ->
            val docRef = cartCollection.document(documentId)
            val doc = transaction.get(docRef)
            val product = doc.toObject(CartProduct::class.java)
            product?.let {cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProduct = cartProduct.copy(quantity = newQuantity)
                transaction.set(docRef, newProduct)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }
    enum class QuantityChanging {
        INCREASE, DECREASE
    }

}