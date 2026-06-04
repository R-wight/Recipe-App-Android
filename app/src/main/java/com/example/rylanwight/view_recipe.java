package com.example.rylanwight;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class view_recipe extends AppCompatActivity {

    Recipe recipe;
    ArrayList<Ingredient> ingredients;
    ArrayList<Instruction> instructions;
    TextView recipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initialize toolbar
        Toolbar toolbar = findViewById(R.id.tbViewRecipe);
        toolbar.setTitle("Recipe Box");
        toolbar.setBackgroundColor(Color.parseColor("#C9C3BD"));
        setSupportActionBar(toolbar);

        //Initialize views
        recipeName = findViewById(R.id.tvRecipeName);

        //Current Recipe
        recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        ingredients = recipe.getIngredients();
        instructions = recipe.getInstructions();

        //Set views
        recipeName.setText(recipe.getName());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) //the parameter is the toolbar we created
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.mnuViewRecipes)
        {
            //Toast.makeText(this, "View Recipes", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class); // this is the activity that we want to start
            this.startActivity(intent);

        }
        else //New recipe was clicked
        {
            Toast.makeText(this, "New Recipe", Toast.LENGTH_LONG).show();
        }
        return true;
    }
}