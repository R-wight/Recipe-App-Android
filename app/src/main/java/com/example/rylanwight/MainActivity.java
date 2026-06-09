package com.example.rylanwight;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecipeSelectedListener
{

    Toolbar toolbar;
    private RecipeDBHelper recipeDB;
    private Cursor recipeCursor;
    private ArrayList<Ingredient> ingredients1;
    private ArrayList<Instruction> instructions1;
    private ArrayList<Ingredient> ingredients2;
    private ArrayList<Instruction> instructions2;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FragmentStateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Obtain the Fragment Manager, this lets us access fragment instances
        //FragmentManager manager = getSupportFragmentManager();
        //Obtain references to the Fragment
        //RecipeListFragment listFragment = (RecipeListFragment) manager.findFragmentById(R.id.listFragment);
        //listFragment.setRecipeSelectedListener(this); //Might not need this

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ingredients1 = new ArrayList<Ingredient>();
        ingredients1.add(new Ingredient(1, "Spaghetti"));
        instructions1 = new ArrayList<>();
        instructions1.add(new Instruction(1, "Boil Pasta"));
        ingredients2 = new ArrayList<Ingredient>();
        ingredients2.add(new Ingredient(2, "T-bone"));
        instructions2 = new ArrayList<>();
        instructions2.add(new Instruction(2, "Turn on grill"));

        toolbar = findViewById(R.id.tbTop);
        toolbar.setTitle("Recipe Box");
        //set the activities action bar to be this object
        setSupportActionBar(toolbar);
        Recipe recipe3 = new Recipe("Pizza", ingredients1, instructions1);
        recipe3.setFavorite(true);

        //DATABASE
        recipeDB = new RecipeDBHelper(this);
        recipeDB.open();
        recipeDB.onUpgrade(recipeDB.sqlDB, 0, 1);
        recipeDB.createRecipe(new Recipe("Pasta", ingredients1, instructions1));
        recipeDB.createRecipe(new Recipe("Steak", ingredients2, instructions2));
        recipeDB.createRecipe(recipe3);
        recipeDB.close();

        recipeDB.open();
        recipeCursor = recipeDB.getAllRecipes();
        if(recipeCursor.moveToFirst())
        {
            do
            {
                Log.i("Row: ", recipeCursor.getString(0) + "," +
                        recipeCursor.getString(1));
            }
            while(recipeCursor.moveToNext());
        }
        recipeDB.close();

        tabLayout = findViewById(R.id.Tabs);
        viewPager = findViewById(R.id.view_pager);

        adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch(position)
                {
                    case 0:
                        return new RecipeListFragment();
                    case 1:
                        return new FavoriteListFragment();
                    default:
                        return new RecipeListFragment();
                }
            };

            @Override
            public int getItemCount() {
                return 2;
            }
        };
        viewPager.setAdapter(adapter);
        //tabLayout.setupWithViewPager(viewPager);

        TabLayoutMediator mediator = new TabLayoutMediator(
                tabLayout,
                viewPager,
                (tab, position) -> {

                    switch (position) {
                        case 0:
                            tab.setText("Recipes");
                            break;
                        case 1:
                            tab.setText("Favorites");
                            break;

                    }
                }
        );
        mediator.attach();



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
            //Toast.makeText(this, "New Recipe", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, new_recipe.class);
            this.startActivity(intent);
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