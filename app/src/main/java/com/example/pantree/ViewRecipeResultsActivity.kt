package com.example.pantree

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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

        val maximizeBtn = findViewById<Button>(R.id.maximizeBtn)
        val minimizeBtn = findViewById<Button>(R.id.minimizeBtn)


        val titleView = findViewById<TextView>(R.id.recipeTitleText)
        val saveBtn = findViewById<Button>(R.id.saveBtn)
        val shareBtn = findViewById<Button>(R.id.shareBtn)

        maximizeBtn.setOnClickListener {
            maximizeBtn.visibility = View.GONE
            minimizeBtn.visibility = View.VISIBLE

            titleView.visibility = View.GONE
            saveBtn.visibility = View.GONE
            shareBtn.visibility = View.GONE


            val params = webView.layoutParams as ViewGroup.MarginLayoutParams
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.topMargin = 0  // remove the top margin
            webView.layoutParams = params
            webView.requestLayout()
        }

        minimizeBtn.setOnClickListener {
            maximizeBtn.visibility = View.VISIBLE
            minimizeBtn.visibility = View.GONE

            titleView.visibility = View.VISIBLE
            saveBtn.visibility = View.VISIBLE
            shareBtn.visibility = View.VISIBLE


            val density = resources.displayMetrics.density
            val params = webView.layoutParams as ViewGroup.MarginLayoutParams
            params.height = (317 * density).toInt()
            params.width = (333 * density).toInt()
            params.topMargin = (150 * density).toInt()
            webView.layoutParams = params
            webView.requestLayout()
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