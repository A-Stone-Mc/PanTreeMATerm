package com.example.pantree

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MyRecipesActivity : AppCompatActivity() {

    private lateinit var savedRecipesList: LinearLayout
    private lateinit var noSavedRecipesText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_recipes)

        savedRecipesList = findViewById(R.id.savedRecipesList)
        noSavedRecipesText = findViewById(R.id.noSavedRecipesText)

        val prefs = getSharedPreferences("recipes", MODE_PRIVATE)
        val savedUrls = prefs.getStringSet("savedRecipes", setOf()) ?: setOf()

        if (savedUrls.isEmpty()) {
            noSavedRecipesText.visibility = View.VISIBLE
        } else {
            noSavedRecipesText.visibility = View.GONE
            for (url in savedUrls) {
                val title = extractTitleFromUrl(url)
                val btn = Button(this)
                btn.text = title
                btn.setOnClickListener {
                    val intent = Intent(this, ViewSavedRecipeActivity::class.java)
                    intent.putExtra("RECIPE_URL", url)
                    startActivity(intent)
                }
                savedRecipesList.addView(btn)
            }
        }
    }

    private fun extractTitleFromUrl(url: String): String {
        return url.trimEnd('/').substringAfterLast('/')
            .replace("-", " ")
            .replaceFirstChar { it.uppercase() }
    }
}