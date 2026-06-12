package com.example.rylanwight;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * I think we should just add ingredients and instructions to the list, when save is clicked create a recipe in the db then get that id
 * and loop through the ingredients and set the id, we may need to add a setter for id in those objects
 *
 *
 */
public class new_recipe extends AppCompatActivity {

    private Recipe recipe;
    private RecipeDBHelper recipeDB;
    private ArrayList<Ingredient> ingredients;
    private RecyclerView ingredientList;
    private RecyclerView.Adapter ingredientAdapter;
    private ArrayList<Instruction> instructions;
    private RecyclerView instructionList;
    private RecyclerView.Adapter instructionAdapter;

    private EditText etIngredient;
    private EditText etInstruction;
    private EditText etTitle;
    private Button btnAddIngredient;
    private Button btnAddInstruction;
    private Button btnClear;
    private Button btnSave;
    private Button btnAddImage;
    private ImageView userImage;
    private Uri imageUri;
    private String imagePath;
    private int initialIngredients;
    private int initialInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //ArrayLists
        ingredients = new ArrayList<>();
        instructions = new ArrayList<>();

        //Buttons
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnAddInstruction = findViewById(R.id.btnAddInstruction);
        btnClear = findViewById(R.id.btnClear);
        btnSave = findViewById(R.id.btnSaveRecipe);
        btnAddImage = findViewById(R.id.btnAddImg);

        //Initialize toolbar
        Toolbar toolbar = findViewById(R.id.tbNewRecipe);
        toolbar.setTitle("Recipe Box");
        setSupportActionBar(toolbar);

        //Edit Texts
        etIngredient = findViewById(R.id.etIngredient);
        etInstruction = findViewById(R.id.etInstruction);
        etTitle = findViewById(R.id.etRecipeName);

        //Images
        userImage = findViewById(R.id.imgRecipe);
        //Database
        recipeDB = new RecipeDBHelper(this);

        //Intent
        Intent edit = getIntent();
        Bundle bundle = edit.getExtras();
        boolean isEdit = bundle.getBoolean("edit");
        long recipeID = bundle.getLong("recipeID");
        if(isEdit)
        {
            getRecipe(recipeID);
            initialIngredients = ingredients.size();
            initialInstructions = instructions.size();
            btnSave.setOnClickListener(v -> updateRecipe());
        }
        else
        {
            btnSave.setOnClickListener(v -> saveRecipe());
        }


        //Ingredients Recycler
        ingredientList = (RecyclerView) findViewById(R.id.ingredientsList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ingredientList.setLayoutManager(manager);
        ingredientAdapter = new IngredientListAdapter(this, ingredients);
        ingredientList.setAdapter(ingredientAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ingredientList.getContext(), manager.getOrientation());
        ingredientList.addItemDecoration(dividerItemDecoration);

        //Instructions Recycler
        instructionList = (RecyclerView) findViewById(R.id.instructionsList);
        LinearLayoutManager instrManager = new LinearLayoutManager(this);
        instructionList.setLayoutManager(instrManager);
        instructionAdapter = new InstructionListAdapter(this, instructions);
        instructionList.setAdapter(instructionAdapter);
        DividerItemDecoration dividerItemDecorationInstr = new DividerItemDecoration(instructionList.getContext(), manager.getOrientation());
        instructionList.addItemDecoration(dividerItemDecorationInstr);

        //Button Listeners
        btnAddIngredient.setOnClickListener(v -> addIngredient());
        btnAddInstruction.setOnClickListener(v -> addInstruction());
        btnClear.setOnClickListener(v -> finish());

        btnAddImage.setOnClickListener(v -> handleImage());





    }

    /**
     * This method sets the elements with data if the user came from clicking the edit recipe button
     */
    public void setFields()
    {
        etTitle.setText(recipe.getName());
        setRecipeImage();
    }

    /**
     * This method sets the image if a user adds one from the add image button
     */
    public void setRecipeImage()
    {
        String imageUri = recipe.getImageUri();
        File imageFile = new File(imageUri);
        if(imageFile.exists())
        {
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri);
            userImage.setImageBitmap(bitmap);
        }
    }

    /**
     * This method updates a recipe in the database if the user is editing a recipe
     */
    public void updateRecipe()
    {
        if(etTitle.getText().toString().trim().length() < 1)
        {
            Toast.makeText(this, "Recipe Name field was left empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe.setName(etTitle.getText().toString());
        recipe.setInstructions(instructions);
        recipe.setIngredients(ingredients);
        if(imagePath != null)
        {
            recipe.setImageUri(imagePath);
        }
        recipeDB.open();
        updateIngredientsWithRecipeID();
        updateInstructionsWithRecipeID();
        saveIngredient();
        saveInstruction();
        recipeDB.updateRecipe(recipe);
        recipeDB.close();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        finish();
    }

    /**
     * This method gets a specific recipe from the database based on their id
     * @param id
     */
    public void getRecipe(long id)
    {
        recipeDB.open();
        Cursor cursor = recipeDB.getRecipe(id);
        if(cursor != null & cursor.moveToFirst())
        {
            do
            {
                recipe = new Recipe(cursor.getString(1));
                recipe.setID(id);
                recipe.setFavorite(cursor.getInt(2) == 1);
                recipe.setImageUri(cursor.getString(3));
            } while(cursor.moveToNext());
        }
        getIngredients();
        getInstructions();
        setFields();
    }

    /**
     * This method gets all ingredients associated with the recipe if the user is editing an existing recipe
     */
    public void getIngredients()
    {
        recipeDB.open();
        Cursor cursor = recipeDB.getAllIngredients(recipe);
        if(cursor != null & cursor.moveToFirst())
        {
            do
            {
                ingredients.add(new Ingredient(cursor.getLong(0),cursor.getLong(1), cursor.getString(2)));
            }
            while(cursor.moveToNext());
        }
    }
    /**
     * This method gets all instructions associated with the recipe if the user is editing an existing recipe
     */
    public void getInstructions()
    {
        recipeDB.open();
        Cursor cursor = recipeDB.getAllInstructions(recipe);
        if(cursor != null & cursor.moveToFirst())
        {
            do
            {
                instructions.add(new Instruction(cursor.getLong(0),cursor.getLong(1), cursor.getString(2)));
            }
            while(cursor.moveToNext());
        }
    }

    /**
     * This method saves a new recipe to the database
     */
    public void saveRecipe()
    {
        if(etTitle.getText().toString().trim().length() < 1)
        {
            Toast.makeText(this, "Recipe Name field was left empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        recipe = new Recipe(etTitle.getText().toString(), new ArrayList<>(), new ArrayList());
        recipe.setImageUri(imagePath);
        recipeDB.open();
        recipeDB.createRecipe(recipe);
        updateIngredientsWithRecipeID();
        updateInstructionsWithRecipeID();
        recipe.setIngredients(ingredients);
        recipe.setInstructions(instructions);
        recipeDB.updateRecipe(recipe);
        saveIngredient();
        saveInstruction();
        recipeDB.close();
        Intent intent = new Intent();
        setResult(5, intent);
        finish();
    }

    /**
     * This method is used to save ingredients to the database, this method is called by saveRecipe
     */
    public void saveIngredient()
    {
        recipeDB.deleteIngredients(recipe);
        for(Ingredient ingredient : ingredients)
        {
            recipeDB.createIngredient(ingredient);
        }
    }
    /**
     * This method is used to save instructions to the database, this method is called by saveRecipe
     */
    public void saveInstruction()
    {
        recipeDB.deleteInstructions(recipe);
        for(Instruction instruction : instructions)
        {
            recipeDB.createInstruction(instruction);
        }
    }

    /**
     * This method updates the list of ingredients with the recipeID so they can be properly stored in the database
     */
    public void updateIngredientsWithRecipeID()
    {
        for(Ingredient ingredient : ingredients)
        {
            ingredient.setRecipeId(recipe.getID());
        }
    }

    /**
     * This method updates the list of instructions with the recipeID so they can be properly stored in the database
     */
    public void updateInstructionsWithRecipeID()
    {
        for(Instruction instruction : instructions)
        {
            instruction.setRecipeId(recipe.getID());
        }
    }

    /**
     * This method adds an ingredient to the ingredients arraylist and clears the corresponding EditText field
     */
    public void addIngredient()
    {
        if(etIngredient.getText().toString().trim().equals(""))
        {
            Toast.makeText(this, "Ingredient field was left empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        ingredients.add(new Ingredient( etIngredient.getText().toString()));
        ingredientAdapter.notifyDataSetChanged();
        etIngredient.setText("");
    }
    /**
     * This method adds an instruction to the instructions arraylist and clears the corresponding EditText field
     */
    public void addInstruction()
    {
        if(etInstruction.getText().toString().trim().equals(""))
        {
            Toast.makeText(this, "Instruction field was left empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        instructions.add(new Instruction(etInstruction.getText().toString()));
        instructionAdapter.notifyDataSetChanged();
        etInstruction.setText("");
    }

    /**
     * This method handles getting an image from the user
     */
    public void handleImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    /**
     * This method saves the image to internal storage so it can be saved to the database
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100)
        {
            if(resultCode == RESULT_OK) //get the users image
            {
                imageUri = data.getData();

                //save to internal storage
                try
                {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imagePath = saveImage(bitmap);
                    userImage.setImageBitmap(bitmap);
                }
                catch(IOException exp)
                {
                    exp.printStackTrace();
                }
            }
        }
    }

    /**
     * This method creates a unique file name and handles the actual compressing and saving of the image
     * @param bitmap
     * @return - the absolute path of the image
     */
    private String saveImage(Bitmap bitmap)
    {
        File obDir = getFilesDir();
        File obFile = new File(obDir, "recipe_" + System.currentTimeMillis() + ".png"); //creates unique file name
        try
        {
            FileOutputStream obOut = new FileOutputStream(obFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, obOut);
            obOut.flush();
            obOut.close();
        }
        catch(IOException exp)
        {
            exp.printStackTrace();
        }
        return obFile.getAbsolutePath();
    }

    //BELOW IS FOR RECYCLERS
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
        public InstructionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = mInflater.inflate(R.layout.instruction_layout, parent, false);
            return new InstructionListAdapter.ViewHolder(view);
        }
        //binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(InstructionListAdapter.ViewHolder holder, int position)
        {
            Instruction instruction = instructions.get(position);
            holder.tvInstruction.setText((position + 1) + ". " + instruction.getInstruction());
            holder.delBtn.setOnClickListener(v -> {
                int currentPosition = holder.getAdapterPosition();
                instructions.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, instructions.size());
            });
        }

        //total number of rows
        @Override
        public int getItemCount(){ return instructions.size(); }
        public Instruction getItem(int id){ return instructions.get(id); }

        //Stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            TextView tvInstruction;
            ImageButton delBtn;
            public ViewHolder(View itemView)
            {
                super(itemView);
                tvInstruction = itemView.findViewById(R.id.tvInstruction);
                delBtn = itemView.findViewById(R.id.btnInsDelete);
                itemView.setOnClickListener(this);
            }
            //Not sure if this is needed either
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
        public IngredientListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = mInflater.inflate(R.layout.ingredient_layout, parent, false);
            return new IngredientListAdapter.ViewHolder(view);
        }
        //binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(IngredientListAdapter.ViewHolder holder, int position)
        {
            Ingredient ingredient = ingredients.get(position);
            holder.tvIngredient.setText((position + 1) + ". " + ingredient.getIngredient());
            holder.delBtn.setOnClickListener(v -> {
                int currentPosition = holder.getAdapterPosition();
                ingredients.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, ingredients.size());
            });
        }

        //total number of rows
        @Override
        public int getItemCount(){ return ingredients.size(); }
        public Ingredient getItem(int id){ return ingredients.get(id); }

        //Stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            TextView tvIngredient;
            ImageButton delBtn;
            public ViewHolder(View itemView)
            {
                super(itemView);
                tvIngredient = itemView.findViewById(R.id.tvIngredient);
                delBtn = itemView.findViewById(R.id.btnIngDelete);
                itemView.setOnClickListener(this);
            }
            //Not sure if this is needed either
            @Override
            public void onClick(View v)
            {
                if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    //BELOW IS FOR TOOLBAR
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