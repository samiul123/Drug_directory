package com.example.samiu.drug_directory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity{

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle toogle;
    public NavigationView navView;
    Toolbar toolbar;
    String drug_id, genericName, tradeName;
    Bundle bundle;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_details:
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(bundle);
                    FragmentTransaction homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    homeFragmentTransaction.replace(R.id.containerId, detailFragment, "DetailFragment");
                    homeFragmentTransaction.commit();
                    return true;
                case R.id.navigation_similar:
                    SimilarFragment similarFragment = new SimilarFragment();
                    similarFragment.setArguments(bundle);
                    FragmentTransaction similarFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    similarFragmentTransaction.replace(R.id.containerId, similarFragment, "SimilarFragment");
                    similarFragmentTransaction.commit();
                    return true;
                /*case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;*/
                case R.id.calculate_dose:
                    CalculateFragment calculateFragment = new CalculateFragment();
                    calculateFragment.setArguments(bundle);
                    FragmentTransaction calculateFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    calculateFragmentTransaction.replace(R.id.containerId, calculateFragment, "CalculateFragment");
                    calculateFragmentTransaction.commit();
                    return true;
                case R.id.similar_fav:
                    SimilarFavFragment similarFavFragment = new SimilarFavFragment();
                    similarFavFragment.setArguments(bundle);
                    FragmentTransaction similarFavFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    similarFavFragmentTransaction.replace(R.id.containerId, similarFavFragment, "SimilarFavFragment");
                    similarFavFragmentTransaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent received_intent = getIntent();
        drug_id = received_intent.getStringExtra("drug_id");
        genericName = received_intent.getStringExtra("genericName");
        tradeName = received_intent.getStringExtra("tradeName");

        bundle = new Bundle();
        bundle.putString("drug_id", drug_id);
        bundle.putString("genericName", genericName);
        bundle.putString("tradeName", tradeName);

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        FragmentTransaction homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
        homeFragmentTransaction.replace(R.id.containerId, detailFragment, "DetailFragment");
        homeFragmentTransaction.commit();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_main){
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuItem item = menu.findItem(R.id.action_main);
        return true;
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.nav_search){
//            drawerLayout.closeDrawers();
//            Intent mainIntent = new Intent(HomeActivity.this, MainActivity.class);
//            startActivity(mainIntent);
//            return true;
//        }
//        if(id == R.id.nav_viewProducts){
//            drawerLayout.closeDrawers();
//            Intent mainIntent = new Intent(HomeActivity.this, MainActivity.class);
//            startActivity(mainIntent);
//            return true;
//        }
//
//        return false;
//    }
}
