package com.example.pantree

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddNewIngredientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_ingredient)

        val input = findViewById<EditText>(R.id.ingredientInput)
        val addBtn = findViewById<Button>(R.id.addIngredientBtn)

        addBtn.setOnClickListener {
            val newIngredient = input.text.toString().trim()

            if (newIngredient.isNotEmpty()) {
                val prefs = getSharedPreferences("ingredients", Context.MODE_PRIVATE)
                val ingredientSet = prefs.getStringSet("myIngredients", mutableSetOf())!!.toMutableSet()
                ingredientSet.add(newIngredient)
                prefs.edit().putStringSet("myIngredients", ingredientSet).apply()

                Toast.makeText(this, "Ingredient added!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter an ingredient", Toast.LENGTH_SHORT).show()
            }
        }
    }
}