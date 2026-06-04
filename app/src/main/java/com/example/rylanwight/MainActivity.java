package com.example.rylanwight;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements RecipeSelectedListener
{

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Obtain the Fragment Manager, this lets us access fragment instances
        FragmentManager manager = getSupportFragmentManager();
        //Obtain references to the Fragment
        RecipeListFragment listFragment = (RecipeListFragment) manager.findFragmentById(R.id.listFragment);
        listFragment.setRecipeSelectedListener(this); //Might not need this

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.tbTop);
        toolbar.setTitle("Recipe Box");
        //set the activities action bar to be this object
        setSupportActionBar(toolbar);


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
            Intent intent = new Intent(this, view_recipe.class); // this is the activity that we want to start
            this.startActivity(intent);

        }
        else //New recipe was clicked
        {
            Toast.makeText(this, "New Recipe", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public void setSelectedRecipe(Recipe recipe)
    {
        Toast.makeText(this, "Open: " + recipe.getName(), Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(this, DetailActivity.class);
        //intent.putExtra("course", course);
        //this.startActivity(intent);
    }
}