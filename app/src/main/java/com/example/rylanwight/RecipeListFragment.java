package com.example.rylanwight;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecipeListFragment extends Fragment
{
    private RecyclerView recipeList;
    private RecyclerView.Adapter recipeAdapter;
    private ArrayList<Recipe> recipes;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Instruction> instructions;
    private RecipeSelectedListener listener;
    private RecipeDBHelper recipeDB;
    private Cursor recipeCursor;


    public void setRecipeSelectedListener(RecipeSelectedListener listener)
    {
        this.listener = listener;
    }

    public void changeRecipe(Recipe recipe)
    {
        listener.setSelectedRecipe(recipe);
    }


    //This is where I will need to get recipes from the database
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        recipeDB = new RecipeDBHelper(getContext());
        recipes = new ArrayList<>();

        //DATABASE
        recipeDB.open();
        recipeCursor = recipeDB.getAllRecipes();
        if(recipeCursor.moveToFirst())
        {
            do
            {
                Recipe recipe = new Recipe(recipeCursor.getString(1));
                recipe.setID(recipeCursor.getLong(0));
                recipes.add(recipe);
            }
            while(recipeCursor.moveToNext());
        }
        recipeDB.close();

        View recipeListLayout = inflater.inflate(R.layout.recipe_list_fragment, container, false);

        recipeList = (RecyclerView) recipeListLayout.findViewById(R.id.recipeList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recipeList.setLayoutManager(manager);
        recipeAdapter = new RecipeListAdapter(getContext(), recipes, new ItemClickListener(){
            @Override
            public void onItemClick(View view, int position)
            {

                Toast.makeText(getContext(), recipes.get(position).getID() + "", Toast.LENGTH_SHORT).show();
                //View the recipe and pass information
                Intent intent = new Intent(view.getContext(), view_recipe.class); // this is the activity that we want to start
                intent.putExtra("recipe", recipes.get(position).getID());
                //view.getContext().startActivity(intent);
                startActivityForResult(intent, 1);

            }
        });
        recipeList.setAdapter(recipeAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recipeList.getContext(), manager.getOrientation());
        recipeList.addItemDecoration(dividerItemDecoration);
        return recipeListLayout;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            if(resultCode == 5)
            {
                refreshRecipes();
            }
        }
    }

    public void refreshRecipes()
    {
        recipes.clear();
        recipeDB.open();
        recipeCursor = recipeDB.getAllRecipes();
        if(recipeCursor.moveToFirst())
        {
            do
            {
                Recipe recipe = new Recipe(recipeCursor.getString(1));
                recipe.setID(recipeCursor.getLong(0));
                recipes.add(recipe);
            }
            while(recipeCursor.moveToNext());
        }
        recipeDB.close();
        recipeAdapter.notifyDataSetChanged();
    }
    public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder>
    {
        private ItemClickListener mClickListener;
        private ArrayList<Recipe> recipes;
        private LayoutInflater mInflater;

        //Constructor gets data and a context
        RecipeListAdapter(Context context, ArrayList<Recipe> recipes, ItemClickListener listener)
        {
            this.mInflater = LayoutInflater.from(context);
            this.recipes = recipes;
            this.mClickListener = listener;
        }

        //inflates the layout we created that will show one row of data
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = mInflater.inflate(R.layout.recipe_layout, parent, false);
            return new ViewHolder(view);
        }
        //binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            Recipe recipe = recipes.get(position);
            holder.tvRecipe.setText(recipe.getName());
        }

        //total number of rows
        @Override
        public int getItemCount()
        {
            return recipes.size();
        }
        public Recipe getItem(int id)
        {
            return recipes.get(id);
        }

        // allows click events to be caught
        public void setClickListener(ItemClickListener itemClickListener)
        {
            this.mClickListener = itemClickListener;
        }

        //Stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            TextView tvRecipe;

            public ViewHolder(View itemView)
            {
                super(itemView);
                tvRecipe = itemView.findViewById(R.id.tvRecipe);
                itemView.setOnClickListener(this);
            }
            @Override
            public void onClick(View v)
            {
                if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

}
