package com.example.pantree

import android.os.Bundle
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

class EditRecipeActivity : AppCompatActivity() {

    private lateinit var recipeUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        recipeUrl = intent.getStringExtra("RECIPE_URL") ?: return

        val titleView = findViewById<TextView>(R.id.editRecipeTitleText)
        val ingredientsInput = findViewById<EditText>(R.id.editIngredientsText)
        val directionsInput = findViewById<EditText>(R.id.editDirectionsText)

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