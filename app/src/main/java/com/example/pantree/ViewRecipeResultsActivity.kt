package com.example.pantree

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ViewRecipeResultsActivity : AppCompatActivity() {

    private lateinit var recipeUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_recipe_results)

        val titleText = intent.getStringExtra("RECIPE_TITLE") ?: "Recipe"
        findViewById<TextView>(R.id.recipeTitleText).text = titleText

        recipeUrl = intent.getStringExtra("RECIPE_URL") ?: return

        val webView = findViewById<WebView>(R.id.savedRecipeWebView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(recipeUrl)

        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            saveRecipe(recipeUrl)
        }

        findViewById<Button>(R.id.shareBtn).setOnClickListener {
            shareRecipe(recipeUrl)
        }
    }

    private fun saveRecipe(url: String) {
        val prefs = getSharedPreferences("recipes", MODE_PRIVATE)
        val saved = prefs.getStringSet("savedRecipes", mutableSetOf())!!.toMutableSet()
        if (saved.contains(url)) {
            Toast.makeText(this, "Already saved", Toast.LENGTH_SHORT).show()
        } else {
            saved.add(url)
            prefs.edit().putStringSet("savedRecipes", saved).apply()
            Toast.makeText(this, "Recipe saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareRecipe(url: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Recipe URL", url)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
    }

}