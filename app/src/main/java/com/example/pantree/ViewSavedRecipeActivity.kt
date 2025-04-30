package com.example.pantree

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class ViewSavedRecipeActivity : AppCompatActivity() {

    private lateinit var recipeUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_saved_recipe)

        recipeUrl = intent.getStringExtra("RECIPE_URL") ?: return

        val webView = findViewById<WebView>(R.id.savedRecipeWebView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(recipeUrl)

        val title = extractTitleFromUrl(recipeUrl)
        findViewById<TextView>(R.id.savedRecipeTitleText).text = title

        val editBtn = findViewById<Button>(R.id.editBtn)


        findViewById<Button>(R.id.unsaveBtn).setOnClickListener {
            val prefs = getSharedPreferences("recipes", MODE_PRIVATE)
            val saved = prefs.getStringSet("savedRecipes", mutableSetOf())!!.toMutableSet()
            if (saved.remove(recipeUrl)) {
                prefs.edit().putStringSet("savedRecipes", saved).apply()
                Toast.makeText(this, "Recipe removed", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Recipe was not saved", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.savedShareBtn).setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Recipe URL", recipeUrl)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        editBtn.setOnClickListener {
            val intent = Intent(this, EditRecipeActivity::class.java)
            intent.putExtra("RECIPE_URL", recipeUrl)
            startActivity(intent)
        }



        val maximizeBtn = findViewById<Button>(R.id.maximizeBtn)
        val minimizeBtn = findViewById<Button>(R.id.minimizeBtn)


        val titleView = findViewById<TextView>(R.id.savedRecipeTitleText)
        val saveBtn = findViewById<Button>(R.id.unsaveBtn)
        val shareBtn = findViewById<Button>(R.id.savedShareBtn)

       //maximize the web view
        maximizeBtn.setOnClickListener {
            maximizeBtn.visibility = View.GONE
            minimizeBtn.visibility = View.VISIBLE

            titleView.visibility = View.GONE
            saveBtn.visibility = View.GONE
            shareBtn.visibility = View.GONE
            editBtn?.visibility = View.GONE

            val params = webView.layoutParams
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            webView.layoutParams = params
            webView.requestLayout()
        }

        minimizeBtn.setOnClickListener {
            maximizeBtn.visibility = View.VISIBLE
            minimizeBtn.visibility = View.GONE

            titleView.visibility = View.VISIBLE
            saveBtn.visibility = View.VISIBLE
            shareBtn.visibility = View.VISIBLE
            editBtn?.visibility = View.VISIBLE

            val density = resources.displayMetrics.density
            val params = webView.layoutParams
            params.height = (317 * density).toInt()
            params.width = (333 * density).toInt()
            webView.layoutParams = params
            webView.requestLayout()
        }





    }

    private fun extractTitleFromUrl(url: String): String {
        return url.trimEnd('/').substringAfterLast('/')
            .replace("-", " ")
            .replaceFirstChar { it.uppercase() }
    }
}