package com.example.pantree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var resultsContainer: LinearLayout
    private lateinit var searchBar: EditText
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        resultsContainer = findViewById(R.id.resultsContainer)
        searchBar = findViewById(R.id.searchBarFieldText2)
        searchButton = findViewById(R.id.searchButton2)

        val initialQuery = intent.getStringExtra("QUERY") ?: ""
        searchBar.setText(initialQuery)
        performSearch(initialQuery)

        searchButton.setOnClickListener {
            val query = searchBar.text.toString()
            performSearch(query)
        }


    }

    private fun performSearch(query: String) {
        resultsContainer.removeAllViews()
        val searchUrl = "https://www.allrecipes.com/search?q=" + query.replace(" ", "+")
        Log.d("PanTree", "Searching URL: $searchUrl")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect(searchUrl).get()

                val recipeCards = doc.select("a[href*=/recipe/]") // All <a> tags linking to recipes
                val added = mutableSetOf<String>()
                val titlesAndLinks = mutableListOf<Pair<String, String>>()

                for (card in recipeCards) {
                    val url = card.absUrl("href")
                    val title = card.selectFirst("span.card__title, span.card__title-text")?.text()?.trim()
                        ?: continue

                    if (url.contains("/recipe/") && title.isNotEmpty() && added.add(url)) {
                        titlesAndLinks.add(title to url)
                        if (titlesAndLinks.size == 10) break
                    }
                }

                withContext(Dispatchers.Main) {
                    if (titlesAndLinks.isEmpty()) {
                        val noResultText = TextView(this@SearchResultsActivity)
                        noResultText.text = "No recipes found."
                        resultsContainer.addView(noResultText)
                    } else {
                        for ((title, link) in titlesAndLinks) {
                            val btn = Button(this@SearchResultsActivity)
                            btn.text = title
                            btn.setOnClickListener {
                                val intent = Intent(this@SearchResultsActivity, ViewRecipeResultsActivity::class.java)
                                intent.putExtra("RECIPE_URL", link)
                                intent.putExtra("RECIPE_TITLE", title)
                                startActivity(intent)
                            }
                            resultsContainer.addView(btn)
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    val errorText = TextView(this@SearchResultsActivity)
                    errorText.text = "Error loading recipes."
                    resultsContainer.addView(errorText)
                }
            }
        }
    }
}