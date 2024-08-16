package com.example.lonelyshop.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.lonelyshop.R
import com.example.lonelyshop.databinding.ActivityShoppingBinding
import com.example.lonelyshop.util.Resource
import com.example.lonelyshop.viewmodels.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    val bingding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }
    val viewModel by viewModels<CartViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bingding.root)

        val navController = findNavController(R.id.shoppingHostFragment)
        bingding.bottomNavigation.setupWithNavController(navController)

      lifecycleScope.launchWhenStarted {
          viewModel.cartProducts.collectLatest {
              when(it)
              {
                  is Resource.Success->{
                        val count = it.data?.size?:0
                      val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
                      bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                          backgroundColor = resources.getColor(R.color.g_blue)
                      }
                  }
                  else->Unit
              }
          }
      }


    }
}