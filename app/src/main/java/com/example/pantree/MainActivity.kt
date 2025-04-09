package com.example.pantree

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    private val randomRecipes = listOf(
        "https://www.allrecipes.com/recipe/232224/balsamic-goat-cheese-stuffed-chicken-breasts/",
        "https://www.allrecipes.com/recipe/223042/chicken-parmesan/",
        "https://www.allrecipes.com/recipe/14554/sirloin-steak-with-garlic-butter/",
        "https://www.allrecipes.com/recipe/10813/best-chocolate-chip-cookies/",
        "https://www.allrecipes.com/recipe/239682/easy-tacos/",
        "https://www.allrecipes.com/recipe/7995/oreo-cookie-cake/",
        "https://www.allrecipes.com/recipe/258809/zucchini-noodles-with-chicken-and-tomatoes-in-a-lemon-garlic-sauce/"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchBar = findViewById<EditText>(R.id.searchBarFieldText)
        val searchBtn = findViewById<Button>(R.id.searchButton)
        val recipeTitleView = findViewById<TextView>(R.id.recipeCardTitle)
        val recipeCard = findViewById<MaterialCardView>(R.id.recipeOfDayCard)

        val randomUrl = randomRecipes.random()
        val recipeTitle = extractTitleFromUrl(randomUrl)


        val recipeImage = findViewById<ImageView>(R.id.recipeThumbnail)


        val recipeImageMap = mapOf(
            "balsamic-goat-cheese-stuffed-chicken-breasts" to "https://www.allrecipes.com/thmb/qpEytAH6vY3IAX0Ud93mVIWwe7k=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/1120650-balsamic-goat-cheese-stuffed-chicken-breasts-mzlez-1x1-1-4a444df7a4314855a9b907e7efd7bb29.jpg",
            "chicken-parmesan" to "https://www.allrecipes.com/thmb/hJswPZp-YxWs70HpfQ4z9i16QdE=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/223042-chicken-parmesan-ddmfs-4x3-10448-7d5c30f8a668460ea971e778eae5f3d4.jpg",
            "sirloin-steak-with-garlic-butter" to "https://www.allrecipes.com/thmb/o00RDjn4K5Z235fmRj5_Ipb__no=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/AR-14554-sirloin-steak-with-garlic-butter-step-01-4x3-ddaf9100f4114e9db7ea49152c522676.jpg",
            "best-chocolate-chip-cookies" to "https://www.allrecipes.com/thmb/Bdf8n6bFxfQQAA-4GojXzqQRVJw=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/best-chocolate-chip-cookies-allrecipes-video-10813-4x3-9298be03a9a845989bd7c5457c9fa60d.jpg",
            "easy-tacos" to "https://www.allrecipes.com/thmb/9XUapVzHHmGFubC1JQ7x3LMd2n0=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/8134094-74bd37f5df634c9c9b323db0dfbb93ad.jpg",
            "oreo-cookie-cake" to "https://www.allrecipes.com/thmb/pZnXugfOqo9KOfWYkgdj5s2y20o=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/7255-dirt-cake-ddmfs-3X4-1858-121f5174119245aeb2cbd9cc024dc5c9.jpg",
            "zucchini-noodles-with-chicken-and-tomatoes-in-a-lemon-garlic-sauce" to "https://www.allrecipes.com/thmb/A1pwl5FDl7CI60ZjDQiRnx2pR1w=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/8093578-7a9c514f4df84d2db00fdc4e494f5c18.jpg"

        )

        val urlKey = randomUrl.trimEnd('/').substringAfterLast('/')
        val thumbnailUrl = recipeImageMap[urlKey]

        Glide.with(this)
            .load(thumbnailUrl)
            .into(recipeImage)

        recipeTitleView.text = recipeTitle

        recipeCard.setOnClickListener {
            val intent = Intent(this, ViewRecipeResultsActivity::class.java)
            intent.putExtra("RECIPE_URL", randomUrl)
            intent.putExtra("RECIPE_TITLE", recipeTitle)
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
        }
    }

    private fun extractTitleFromUrl(url: String): String {
        return url.trimEnd('/')
            .substringAfterLast('/')
            .replace("-", " ")
            .replaceFirstChar { it.uppercase() }
    }
}
