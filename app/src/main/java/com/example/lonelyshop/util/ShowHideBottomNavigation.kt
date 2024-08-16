package com.example.lonelyshop.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.lonelyshop.R
import com.example.lonelyshop.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigation(){
    val bottomNavigationViews = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation)
    bottomNavigationViews.visibility = View.GONE
}
fun Fragment.showBottomNavigation(){
    val bottomNavigationViews = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation)
    bottomNavigationViews.visibility = View.VISIBLE

}