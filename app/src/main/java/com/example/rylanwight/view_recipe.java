package com.example.rylanwight;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.File;
import java.util.ArrayList;

public class view_recipe extends AppCompatActivity {

    private Recipe recipe;
    private RecipeDBHelper recipeDB;
    private ArrayList<Ingredient> ingredients;
    private RecyclerView ingredientList;
    private RecyclerView.Adapter ingredientAdapter;
    private ArrayList<Instruction> instructions;
    private RecyclerView instructionList;
    private RecyclerView.Adapter instructionAdapter;
    private TextView recipeName;
    private Button btnDel;
    private Button btnBack;
    private Button btnEdit;
    private ImageButton btnFav;
    private ImageView recipeImg;
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
        //toolbar.setBackgroundColor(Color.parseColor("#C9C3BD"));
        setSupportActionBar(toolbar);

        //Database
        recipeDB = new RecipeDBHelper(this);

        //Initialize views
        recipeName = findViewById(R.id.tvRecipeName);
        recipeImg = findViewById(R.id.imgViewRecipe);

        //Current Recipe
        long recipeID = getIntent().getLongExtra("recipe", 0);
        recipe = getRecipe(recipeID);
        recipe.setID(recipeID);
        recipe.setFavorite(isFavorite(recipeID));
        ingredients = new ArrayList<>();
        instructions = new ArrayList<>();
        getIngredients();
        getInstructions();
        recipe.setInstructions(instructions);
        recipe.setIngredients(ingredients);

        //Set views
        recipeName.setText(recipe.getName());
        setRecipeImage();

        //Ingredient list recycler
        ingredientList = (RecyclerView) findViewById(R.id.ingredientList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientList.setLayoutManager(manager);
        ingredientAdapter = new IngredientListAdapter(this, ingredients);
        ingredientList.setAdapter(ingredientAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ingredientList.getContext(), manager.getOrientation());
        ingredientList.addItemDecoration(dividerItemDecoration);

        //Instruction list recycler
        instructionList = (RecyclerView) findViewById(R.id.instructionList);
        LinearLayoutManager instrManager = new LinearLayoutManager(this);
        instructionList.setLayoutManager(instrManager);
        instructionAdapter = new InstructionListAdapter(this, instructions);
        instructionList.setAdapter(instructionAdapter);
        DividerItemDecoration dividerItemDecorationInstr = new DividerItemDecoration(instructionList.getContext(), manager.getOrientation());
        instructionList.addItemDecoration(dividerItemDecorationInstr);

        //Buttons
        btnDel = findViewById(R.id.btnDelete);
        btnDel.setOnClickListener(v -> deleteRecipe());
        btnFav = findViewById(R.id.btnFavorite);
        btnFav.setOnClickListener(v -> updateRecipe(recipe));
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> editRecipe());

        //Set favorite image
        if (recipe.getFavorite())
        {
            btnFav.setImageResource(android.R.drawable.btn_star_big_on);
        }
        else
        {
            btnFav.setImageResource(android.R.drawable.btn_star_big_off);
        }


    }

    public void deleteRecipe()
    {
        recipeDB.open();
        recipeDB.deleteRecipe(recipe);
        recipeDB.close();
        Intent intent = new Intent();
        setResult(5, intent);
        finish();
    }
    public void updateRecipe(Recipe recipe)
    {
        recipe.setFavorite(!recipe.getFavorite());
        recipeDB.open();
        recipeDB.updateRecipe(recipe);
        recipeDB.close();
        if (recipe.getFavorite())
        {
            btnFav.setImageResource(android.R.drawable.btn_star_big_on);
        }
        else
        {
            btnFav.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }
    public Recipe getRecipe(long id)
    {
        recipeDB.open();
        Cursor cursor = recipeDB.getRecipe(id);
        Recipe recipe = new Recipe(cursor.getString(1));
        recipe.setImageUri(cursor.getString(3));
        cursor.close();
        recipeDB.close();
        return recipe;
    }

    public Boolean isFavorite(long id)
    {
        recipeDB.open();
        Cursor cursor = recipeDB.getRecipe(id);
        return cursor.getInt(2) == 1;
    }
    public void getIngredients()
    {
        recipeDB.open();
        Cursor cursor = recipeDB.getAllIngredients(recipe);
        if(cursor != null & cursor.moveToFirst()) {
            do {
                ingredients.add(new Ingredient(cursor.getLong(0), cursor.getLong(1), cursor.getString(2)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        recipeDB.close();
    }

    public void getInstructions()
    {
        recipeDB.open();
        Cursor cursor = recipeDB.getAllInstructions(recipe);
        if(cursor != null & cursor.moveToFirst())
        {
            do
            {
                instructions.add(new Instruction(cursor.getLong(0), cursor.getLong(1), cursor.getString(2)));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        recipeDB.close();
    }

    public void setRecipeImage()
    {
        String imageUri = recipe.getImageUri();
        File imageFile = new File(imageUri);
        if(imageFile.exists())
        {
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri);
            recipeImg.setImageBitmap(bitmap);
        }
    }

    public void loadRecipes(long id)
    {
        recipe = getRecipe(id);
        recipe.setID(id);
        recipe.setFavorite(isFavorite(id));
        ingredients.clear();
        instructions.clear();
        getIngredients();
        getInstructions();
        ingredientAdapter.notifyDataSetChanged();
        instructionAdapter.notifyDataSetChanged();
    }

    public void editRecipe()
    {
        Intent intent = new Intent(this, new_recipe.class);
        intent.putExtra("edit", true);
        intent.putExtra("recipeID", recipe.getID());
        this.startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK) //Recipe Created
            {
                loadRecipes(recipe.getID());
            }
        }
    }
    public class InstructionListAdapter extends RecyclerView.Adapter<InstructionListAdapter.ViewHolder>
    {
        private ItemClickListener mClickListener;
        private ArrayList<Instruction> instructions;
        private LayoutInflater mInflater;

        InstructionListAdapter(Context context, ArrayList<Instruction> instructions)
        {
            this.mInflater = LayoutInflater.from(context);
            this.instructions = instructions;

        }

        //inflates the layout we created that will show one row of data
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = mInflater.inflate(R.layout.instruction_layout_view_recipe, parent, false);
            return new ViewHolder(view);
        }
        //binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            Instruction instruction = instructions.get(position);
            holder.tvInstruction.setText((position + 1) + ". " + instruction.getInstruction());
        }

        //total number of rows
        @Override
        public int getItemCount(){ return instructions.size(); }
        public Instruction getItem(int id){ return instructions.get(id); }

        //Stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            TextView tvInstruction;
            public ViewHolder(View itemView)
            {
                super(itemView);
                tvInstruction = itemView.findViewById(R.id.tvInstruction);
                itemView.setOnClickListener(this);
            }
            @Override
            public void onClick(View v)
            {
                if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
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
        }

        //inflates the layout we created that will show one row of data
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = mInflater.inflate(R.layout.ingredient_layout_view_recipe, parent, false);
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
                itemView.setOnClickListener(this);
            }

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
            Intent intent = new Intent(this, MainActivity.class); // this is the activity that we want to start
            this.startActivity(intent);

        }
        else //New recipe was clicked
        {
            Intent intent = new Intent(this, new_recipe.class);
            intent.putExtra("edit", false);
            this.startActivity(intent);
        }
        return true;
    }
}