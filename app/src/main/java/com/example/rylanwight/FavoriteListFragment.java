package com.example.rylanwight;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoriteListFragment extends Fragment
{
    private RecyclerView recipeList;
    private RecyclerView.Adapter recipeAdapter;
    private ArrayList<Recipe> recipes;
    private RecipeDBHelper recipeDB;
    private Cursor recipeCursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        recipeDB = new RecipeDBHelper(getContext());
        recipes = new ArrayList<>();

        //DATABASE
        getListFromDB();

        //Recycler
        View recipeListLayout = inflater.inflate(R.layout.recipe_list_fragment, container, false);

        recipeList = (RecyclerView) recipeListLayout.findViewById(R.id.recipeList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recipeList.setLayoutManager(manager);
        recipeAdapter = new FavListAdapter(getContext(), recipes, new ItemClickListener(){
            @Override
            public void onItemClick(View view, int position)
            {
                //View the recipe and pass information
                Intent intent = new Intent(view.getContext(), view_recipe.class); // this is the activity that we want to start
                intent.putExtra("recipe", recipes.get(position).getID()); //This passes the ID of the recipe that was chosen to view_recipe
                startActivityForResult(intent, 1);
            }
        });
        recipeList.setAdapter(recipeAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recipeList.getContext(), manager.getOrientation());
        recipeList.addItemDecoration(dividerItemDecoration);
        return recipeListLayout;
    }

    /**
     * This method listens to see if it needs to refresh the list of favorites
     *
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            if(resultCode == 5)
            {
                getListFromDB();
                recipeAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * This method gets all favorited recipes from the database and adds them to an arraylist to be shown in the
     * recycler view
     */
    public void getListFromDB()
    {
        recipes.clear();
        recipeDB.open();
        recipeCursor = recipeDB.getAllFavorites();
        if(recipeCursor.moveToFirst())
        {
            do
            {
                Recipe recipe = new Recipe(recipeCursor.getString(1));
                recipe.setID(recipeCursor.getLong(0));
                recipe.setFavorite(recipeCursor.getInt(1) == 1);
                recipes.add(recipe);
            }
            while(recipeCursor.moveToNext());
        }
        recipeDB.close();
    }

    //RECYCLER VIEW BELOW
    public class FavListAdapter extends RecyclerView.Adapter<FavListAdapter.ViewHolder>
    {
        private ItemClickListener mClickListener;
        private ArrayList<Recipe> recipes;
        private LayoutInflater mInflater;

        //Constructor gets data and a context
        FavListAdapter(Context context, ArrayList<Recipe> recipes, ItemClickListener listener)
        {
            this.mInflater = LayoutInflater.from(context);
            this.recipes = recipes;
            this.mClickListener = listener;
        }

        //inflates the layout we created that will show one row of data
        @Override
        public FavListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = mInflater.inflate(R.layout.favorite_layout, parent, false);
            return new FavListAdapter.ViewHolder(view);
        }
        //binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(FavListAdapter.ViewHolder holder, int position)
        {
            Recipe recipe = recipes.get(position);
            holder.tvFav.setText(recipe.getName());
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
            TextView tvFav;

            public ViewHolder(View itemView)
            {
                super(itemView);
                tvFav = itemView.findViewById(R.id.tvFav);
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
