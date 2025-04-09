package com.example.pantree

import android.content.Context
import android.os.Bundle
import android.print.PrintManager
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.FileOutputStream

class EditRecipeActivity : AppCompatActivity() {

    private lateinit var recipeUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        recipeUrl = intent.getStringExtra("RECIPE_URL") ?: return

        val titleView = findViewById<TextView>(R.id.editRecipeTitleText)
        val ingredientsInput = findViewById<EditText>(R.id.editIngredientsText)
        val directionsInput = findViewById<EditText>(R.id.editDirectionsText)
        val printBtn = findViewById<Button>(R.id.printButton)

        val prefs = getSharedPreferences("editedRecipes", MODE_PRIVATE)
        val savedIngredients = prefs.getString("${recipeUrl}_ingredients", null)
        val savedDirections = prefs.getString("${recipeUrl}_directions", null)

        if (savedIngredients != null || savedDirections != null) {
            ingredientsInput.setText(savedIngredients ?: "")
            directionsInput.setText(savedDirections ?: "")
        }

        val title = recipeUrl.trimEnd('/').substringAfterLast('/')
            .replace("-", " ")
            .replaceFirstChar { it.uppercase() }

        titleView.text = title

        //parsing technique from youtube
        lifecycleScope.launch(Dispatchers.IO) {
            val doc = Jsoup.connect(recipeUrl).get()

            val ingredients = doc.select("li.mm-recipe-ingredients__list-item, li.mm-recipes-structured-ingredients__list-item")
                .map { it.text().trim() }

            val directions = doc.select("li.comp.mntl-sc-block-group--LI p")
                .map { it.text().trim() }

            val ingredientText = ingredients.joinToString("\n") { "- $it" }
            val directionText = directions.mapIndexed { i, step -> "Step ${i + 1}: $step" }.joinToString("\n\n")

            withContext(Dispatchers.Main) {
                ingredientsInput.setText(ingredientText)
                directionsInput.setText(directionText)
            }
        }

        //print function from stackOverflow
        printBtn.setOnClickListener {
            Log.d("PrintDebug", "Print button clicked")

            val ingredientsHtml = ingredientsInput.text.toString()
                .split("\n")
                .filter { it.isNotBlank() }
                .joinToString("\n") { "<li>${it.trim()}</li>" }

            val directionsHtml = directionsInput.text.toString()
                .split(Regex("(?i)(step\\s\\d+:)"))
                .filter { it.trim().isNotEmpty() }
                .joinToString("\n") { "<li>${it.trim()}</li>" }

            val fullText = """
    <h2>Ingredients</h2>
    <ul>$ingredientsHtml</ul>
    <h2>Directions</h2>
    <ol>$directionsHtml</ol>
""".trimIndent()

            val html = """
        <html>
        <body style='padding: 20px; font-family: sans-serif;'>
            $fullText
        </body>
        </html>
    """.trimIndent()

            val webView = WebView(this)
            webView.settings.javaScriptEnabled = false


            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String?) {
                    Log.d("PrintDebug", "WebView finished loading, starting print")

                    val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
                    val printAdapter = webView.createPrintDocumentAdapter("PanTree_Recipe")

                    printManager.print("PanTree Recipe", printAdapter, null)
                }
            }


            webView.loadDataWithBaseURL(null, html, "text/HTML", "UTF-8", null)
        }

        findViewById<Button>(R.id.saveEditButton).setOnClickListener{
            val editedIngredients = ingredientsInput.text.toString()
            val editedDirections = directionsInput.text.toString()

            val prefs = getSharedPreferences("editedRecipes", MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("${recipeUrl}_ingredients", editedIngredients)
            editor.putString("${recipeUrl}_directions", editedDirections)
            editor.apply()

            Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<Button>(R.id.cancelEditButton).setOnClickListener {
            finish()
        }
    }
}