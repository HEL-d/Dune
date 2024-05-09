package com.evw.aster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShopAdapter():PagingDataAdapter<shopclass,ShopAdapter.shopViewHolder>(Companion) {

    companion object: DiffUtil.ItemCallback<shopclass>(){
        override fun areItemsTheSame(oldItem: shopclass, newItem: shopclass): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: shopclass, newItem: shopclass): Boolean {
           return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: shopViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): shopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_layout,parent,false)
        return shopViewHolder(view)
    }


    class shopViewHolder(itemview:View):RecyclerView.ViewHolder(itemview){
        val gridwebview:WebView = itemview.findViewById(R.id.grid_webview)
         val postername = "file:///android_asset/poster.svg"


        fun bind(item:shopclass){
           val user = item.link

            gridwebview.settings.apply {
                  javaScriptEnabled = true
                  domStorageEnabled = true
                  loadWithOverviewMode = true
              }
              gridwebview.loadUrl("file:///android_asset/Astrshopview.html")
              gridwebview.webViewClient = object: WebViewClient(){
                  override fun onPageFinished(view: WebView?, url: String?) {
                      super.onPageFinished(view, url)
                      gridwebview.evaluateJavascript("javascript: " +"setposter(\"" + postername + "\")",null)
                      gridwebview.evaluateJavascript("javascript: " +"settron(\"" + user + "\")",null)

                  }
              }



          }




    }


}