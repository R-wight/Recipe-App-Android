package com.example.rylanwight;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

        //Initialize toolbar
        Toolbar toolbar = findViewById(R.id.tbNewRecipe);
        toolbar.setTitle("Recipe Box");
        setSupportActionBar(toolbar);

        //Edit Texts
        etIngredient = findViewById(R.id.etIngredient);
        etInstruction = findViewById(R.id.etInstruction);
        etTitle = findViewById(R.id.etRecipeName);

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
        btnClear.setOnClickListener(v -> clearRecipe());
        btnSave.setOnClickListener(v -> saveRecipe());

        //Database
        recipeDB = new RecipeDBHelper(this);

    }


    public void saveRecipe()
    {
        recipe = new Recipe(etTitle.getText().toString(), new ArrayList<>(), new ArrayList());
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

    public void saveIngredient()
    {
        for(Ingredient ingredient : ingredients)
        {
            recipeDB.createIngredient(ingredient);
        }
    }

    public void saveInstruction()
    {
        for(Instruction instruction : instructions)
        {
            recipeDB.createInstruction(instruction);
        }
    }
    public void updateIngredientsWithRecipeID()
    {
        for(Ingredient ingredient : ingredients)
        {
            ingredient.setRecipeId(recipe.getID());
        }
    }

    public void updateInstructionsWithRecipeID()
    {
        for(Instruction instruction : instructions)
        {
            instruction.setRecipeId(recipe.getID());
        }
    }
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
     * Maybe change this to cancel?
     */
    public void clearRecipe()
    {
        finish();
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
            //this.mClickListener = listener;
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
                itemView.setOnClickListener(this); //Not sure if this will be needed
            }
            //Not sure if this is needed either
            @Override
            public void onClick(View v)
            {
                if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
                Toast.makeText(v.getContext(), "View Recipes", Toast.LENGTH_LONG).show();
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
            //this.mClickListener = listener;
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
}