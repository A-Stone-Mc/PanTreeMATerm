package com.example.pantree

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val randomRecipes = listOf(
        "https://www.allrecipes.com/recipe/232224/balsamic-goat-cheese-stuffed-chicken-breasts/",
        "https://www.allrecipes.com/recipe/223042/chicken-parmesan/",
        "https://www.simplyrecipes.com/recipes/banana_bread/",
        "https://www.ambitiouskitchen.com/seriously-the-best-healthy-turkey-chili/",
        "https://thecozycook.com/easy-lasagna-recipe/",
        "https://www.threeolivesbranch.com/copycat-olive-garden-steak-gorgonzola-alfredo/",
        "https://www.allrecipes.com/recipe/258809/zucchini-noodles-with-chicken-and-tomatoes-in-a-lemon-garlic-sauce/"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBar = findViewById<EditText>(R.id.searchBarFieldText)
        val searchBtn = findViewById<Button>(R.id.searchButton)
        val recipeWebView = findViewById<WebView>(R.id.recipeOfDayWebView)
        val randomUrl = randomRecipes.random()

        recipeWebView.settings.javaScriptEnabled = true
        recipeWebView.loadUrl(randomUrl)

        /*recipeWebView.setOnClickListener {
            val intent = Intent(this, ViewRecipeResultActivity::class.java)
            intent.putExtra("RECIPE_URL", randomUrl)
            startActivity(intent)
        }

        searchBtn.setOnClickListener {
            val query = searchBar.text.toString()
            val intent = Intent(this, SearchResultsActivity::class.java)
            intent.putExtra("QUERY", query)
            startActivity(intent)
        }

        findViewById<Button>(R.id.myRecipesButton).setOnClickListener {
            startActivity(Intent(this, MyRecipesActivity::class.java))
        }

        findViewById<Button>(R.id.myIngredientsButton).setOnClickListener {
            startActivity(Intent(this, MyIngredientsActivity::class.java))
        */
    }
}