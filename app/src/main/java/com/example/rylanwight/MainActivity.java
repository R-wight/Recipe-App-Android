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

public class MainActivity extends AppCompatActivity
{

    Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FragmentStateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //TOOLBAR
        toolbar = findViewById(R.id.tbTop);
        toolbar.setTitle("Recipe Box");
        //set the activities action bar to be this object
        setSupportActionBar(toolbar);

        //TAB LAYOUT
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
            Intent intent = new Intent(this, MainActivity.class); // this is the activity that we want to start
            this.startActivity(intent);

        }
        else //New recipe was clicked
        {
            Intent intent = new Intent(this, new_recipe.class);
            intent.putExtra("edit", false); //This tells the new_recipe page that it is a new recipe being created
            this.startActivityForResult(intent, 1);
        }
        return true;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        viewPager.getAdapter().notifyDataSetChanged();
    }


    /**
     * This method is used to update the lists when new recipes are created
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
            if(resultCode == 5) //Recipe Created
            {
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
            }
        }
    }
}