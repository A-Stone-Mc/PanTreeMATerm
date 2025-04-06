package com.example.pantree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MyIngredientsActivity : AppCompatActivity() {

    private lateinit var ingredientListLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ingredients)

        ingredientListLayout = findViewById(R.id.ingredientsListLayout)

        findViewById<Button>(R.id.addIngredientsBtn).setOnClickListener {
            startActivity(Intent(this, AddNewIngredientActivity::class.java))
        }

        findViewById<Button>(R.id.searchByIngredientsBtn).setOnClickListener {
            val prefs = getSharedPreferences("ingredients", MODE_PRIVATE)
            val ingredients = prefs.getStringSet("myIngredients", setOf())!!.toList()
            val query = ingredients.joinToString("+") { it.trim().replace(" ", "+") }
            Log.d("PanTree", "Ingredients used: $ingredients")

            val intent = Intent(this, SearchResultsActivity::class.java)
            intent.putExtra("QUERY", query)
            startActivity(intent)
        }

        findViewById<Button>(R.id.clearAllIngredientsBtn).setOnClickListener {
            val prefs = getSharedPreferences("ingredients", MODE_PRIVATE)
            prefs.edit().remove("myIngredients").apply()
            Toast.makeText(this, "All ingredients cleared.", Toast.LENGTH_SHORT).show()
            loadIngredients()
        }
    }

    override fun onResume() {
        super.onResume()
        loadIngredients()
    }

    private fun loadIngredients() {
        ingredientListLayout.removeAllViews()

        val prefs = getSharedPreferences("ingredients", MODE_PRIVATE)
        val ingredients = prefs.getStringSet("myIngredients", setOf()) ?: setOf()

        if (ingredients.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "No ingredients added."
            ingredientListLayout.addView(emptyText)
        } else {
            for (ingredient in ingredients) {
                val textView = TextView(this)
                textView.text = "• $ingredient"
                textView.textSize = 16f
                textView.setPadding(8, 16, 8, 16)
                textView.setOnClickListener {
                    removeIngredient(ingredient)
                }
                ingredientListLayout.addView(textView)
            }
        }
    }

    private fun removeIngredient(ingredient: String) {
        val prefs = getSharedPreferences("ingredients", MODE_PRIVATE)
        val ingredients = prefs.getStringSet("myIngredients", mutableSetOf())!!.toMutableSet()

        ingredients.remove(ingredient)
        prefs.edit().putStringSet("myIngredients", ingredients).apply()

        Toast.makeText(this, "\"$ingredient\" removed.", Toast.LENGTH_SHORT).show()
        loadIngredients()
    }
}