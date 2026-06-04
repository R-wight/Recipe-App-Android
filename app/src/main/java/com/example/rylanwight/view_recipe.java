package com.example.rylanwight;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class view_recipe extends AppCompatActivity {

    private Recipe recipe;
    private ArrayList<Ingredient> ingredients;
    private RecyclerView ingredientList;
    private RecyclerView.Adapter ingredientAdapter;
    private ArrayList<Instruction> instructions;
    private TextView recipeName;

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
        //LayoutInflater inflater;
        //View ingredientListLayout = inflater.inflate()
        ingredientList = (RecyclerView) findViewById(R.id.ingredientList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientList.setLayoutManager(manager);
        ingredientAdapter = new IngredientListAdapter(this, ingredients);
        ingredientList.setAdapter(ingredientAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ingredientList.getContext(), manager.getOrientation());
        ingredientList.addItemDecoration(dividerItemDecoration);

    }

    public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter.ViewHolder>
    {
        private ItemClickListener mClickListener;
        private ArrayList<Ingredient> ingredients;
        private LayoutInflater mInflater;

        IngredientListAdapter(Context context, ArrayList<Ingredient> ingredients)
        {
            this.mInflater = LayoutInflater.from(context);
            this.ingredients = ingredients;
            //this.mClickListener = listener;
        }

        //inflates the layout we created that will show one row of data
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = mInflater.inflate(R.layout.ingredient_layout, parent, false);
            return new ViewHolder(view);
        }
        //binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            Ingredient ingredient = ingredients.get(position);
            holder.tvIngredient.setText((position + 1) + ". " + ingredient.getIngredient());
        }

        //total number of rows
        @Override
        public int getItemCount(){ return ingredients.size(); }
        public Ingredient getItem(int id){ return ingredients.get(id); }

        //Stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            TextView tvIngredient;
            public ViewHolder(View itemView)
            {
                super(itemView);
                tvIngredient = itemView.findViewById(R.id.tvIngredient);
                itemView.setOnClickListener(this); //Not sure if this will be needed
            }
            //Not sure if this is needed either
            @Override
            public void onClick(View v)
            {
                if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
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