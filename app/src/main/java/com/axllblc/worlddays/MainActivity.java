package com.axllblc.worlddays;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.axllblc.worlddays.databinding.ActivityMainBinding;
import com.axllblc.worlddays.ui.HomeFragment;

import java.time.LocalDate;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    /** Callback to handle back button pressed when Search View is open. */
    private OnBackPressedCallback searchViewOnBackPressedCallback;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Search Bar & Search View
        setSupportActionBar(binding.searchBar);
        binding.searchView.setupWithSearchBar(binding.searchBar);

        searchViewOnBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.searchView.isShowing()) {
                    binding.searchView.hide();
                } else {
                    // If the Search View is closed, remove this callback and call onBackPressed again
                    searchViewOnBackPressedCallback.remove();
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        };

        binding.searchBar.setNavigationOnClickListener(v -> openSearchView());
        binding.searchBar.setOnClickListener(v -> openSearchView());


        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        navController = Objects.requireNonNull(navHostFragment).getNavController();

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);


        binding.fabToday.setOnClickListener(view -> navController.navigate(R.id.back_to_today));
    }

    public void navigateToHome(LocalDate date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(HomeFragment.ARG_DATE, date);
        navController.navigate(R.id.back_to_today, bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSearchView() {
        binding.searchView.show();

        // Close Search View when the back button is pressed
        getOnBackPressedDispatcher().addCallback(searchViewOnBackPressedCallback);
    }
}