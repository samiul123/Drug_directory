package com.example.samiu.drug_directory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import com.google.firebase.iid.FirebaseInstanceId;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.sql.SQLClientInfoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    private DatabaseReference mDatabase;
    private DatabaseReference suggestionRef;
    private DatabaseReference searchResultsRef;
    public static RecyclerView recyclerView;
    private RecyclerView horizontal_recycler_view;
    public static ProgressDialog mDialogue;
    ArrayList<String> tradeNames = new ArrayList<>();
    ArrayList<String> genericNames = new ArrayList<>();
    public static ArrayList<Drug> productList = new ArrayList<>();
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle toogle;
    public NavigationView navView;
    Toolbar toolbar;
    public static TextView noResults;
    private List<UpperView> data;
    VerticalAdapter verticalAdapter;
    DatabaseReference fav_data;
    ArrayList<String> names = new ArrayList<>();
    MaterialSearchView materialSearchView;
    UpperHorizontalAdapter upperHorizontalAdapter;
    public static SQLiteDatabase mySQLiteDb;
    public static ArrayList<String> favList;
    public static String deviceToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        //recyclerView
        recyclerView = findViewById(R.id.drug_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noResults = findViewById(R.id.noResultId);
        /*horizontal_recycler_view = findViewById(R.id.upper_recycler_view);
        horizontal_recycler_view.setHasFixedSize(true);
        horizontal_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        data = fill_with_data();
        upperHorizontalAdapter = new UpperHorizontalAdapter(data, getApplicationContext());
        horizontal_recycler_view.setAdapter(upperHorizontalAdapter);
*/
        deviceToken  = FirebaseInstanceId.getInstance().getToken();
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
        marginLayoutParams.setMargins(0, 80, 0, 0);
        recyclerView.setLayoutParams(marginLayoutParams);

        //Configuring fireBase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("drug_table");
        suggestionRef = FirebaseDatabase.getInstance().getReference().child("drug_table");
        mDatabase.keepSynced(true);
        suggestionRef.keepSynced(true);
        suggestionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                collectNames((Map<String, Object>)dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        searchResultsRef = FirebaseDatabase.getInstance().getReference().child("drug_table");
        searchResultsRef.keepSynced(true);
        //progress
        mDialogue = new ProgressDialog(this);

        //SQLite db
        mySQLiteDb = openOrCreateDatabase("favourite", MODE_PRIVATE, null);
        mySQLiteDb.execSQL("CREATE TABLE IF NOT EXISTS myFavouriteH(id VARCHAR, genericName VARCHAR, companyName VARCHAR, tradeName VARCHAR);");
        //mySQLiteDb.execSQL("INSERT INTO myFavourite values('asd')");
        Cursor result = mySQLiteDb.rawQuery("SELECT * FROM myFavouriteH", null);
        favList = new ArrayList<>();
        while(result.moveToNext()){
            String id = result.getString(0);
            //Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
            favList.add(id);
        }

/*        fav_data = FirebaseDatabase.getInstance().getReference().child("Fav_data").child(deviceToken);
//        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        fav_data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    System.out.println(child.getKey());
                    for(DataSnapshot childOfChild: child.getChildren()){
                        String key = childOfChild.getKey();
                        String value = (String) childOfChild.getValue();
                        System.out.println("in favList 1" + key +" " +  value);
                        if(key.equals("drug_id")){
                            favList.add(value);
                            System.out.println("in favList: " + value);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
        materialSearchView = findViewById(R.id.search_view_id);
        materialSearchView.setVoiceSearch(true);

        navView = findViewById(R.id.nav_id);
        navView.setNavigationItemSelectedListener(this);

        toolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);
        searchViewCode();

        drawerLayout = findViewById(R.id.mainDrawer);
        toogle = new ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    private void collectNames(Map<String, Object> value) {
        for(Map.Entry<String, Object> entry: value.entrySet()){
            Map singleName = (Map) entry.getValue();
            names.add((String) singleName.get("tradeName"));
            tradeNames.add((String) singleName.get("tradeName"));
            if(!names.contains(singleName.get("genericName"))){
                names.add((String) singleName.get("genericName"));
                genericNames.add((String) singleName.get("genericName"));
            }
        }
        System.out.println(names);
        String[] suggestions = names.toArray(new String[names.size()]);
        materialSearchView.setSuggestions(suggestions);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    materialSearchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(verticalAdapter != null){
            verticalAdapter.cleanup();
        }

    }

    private void searchViewCode() {
        materialSearchView.setEllipsize(true);
        materialSearchView.showVoice(true);
        materialSearchView.setCursorDrawable(R.drawable.cursor_drawable);
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(tradeNames.contains(query)){
                    String gen_name;
                    for(Drug drug: productList){
                        if(drug.getTradeName().equals(query)){
                            gen_name = drug.getGenericName();
                            final Query query1 = searchResultsRef.orderByChild("genericName").equalTo(gen_name);
                            query1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() != null){
                                        noResults.setVisibility(View.GONE);
                                        searchGenericResultShow(query1);
                                    }
                                    else{
                                        noResults.setVisibility(View.VISIBLE);
                                        noResults.setText("No results found!");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                }

                else if(genericNames.contains(query)){
                    final Query query1 = searchResultsRef.orderByChild("genericName").equalTo(query);
                    query1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() != null){
                                noResults.setVisibility(View.GONE);
                                searchGenericResultShow(query1);
                            }
                            else{
                                noResults.setVisibility(View.VISIBLE);
                                noResults.setText("No results found!");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    recyclerView.setAdapter(null);
                    noResults.setVisibility(View.VISIBLE);
                    noResults.setText("No results found!");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });
    }

    public void searchGenericResultShow(Query query){
        if(verticalAdapter != null){
            verticalAdapter.cleanup();
        }
        verticalAdapter = new VerticalAdapter(Drug.class,
                R.layout.durg_list_row,
                VerticalAdapter.DrugViewHolder.class,
                query,getApplicationContext(), 0);
        recyclerView.setAdapter(verticalAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mDialogue.setMessage("Please wait...");
        mDialogue.show();
        recyclerViewShow(mDatabase);
    }

    public void recyclerViewShow(final DatabaseReference ref){
//        mDialogue.setMessage("Please wait...");
//        mDialogue.show();
        if(verticalAdapter != null){
            verticalAdapter.cleanup();
        }
//        mDialogue.dismiss();
        verticalAdapter = new VerticalAdapter(Drug.class,
                R.layout.durg_list_row,
                VerticalAdapter.DrugViewHolder.class,
                ref,getApplicationContext(), 1, productList);

        recyclerView.setAdapter(verticalAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!toogle.onOptionsItemSelected(item)){
            return true;
        }
        if(item.getItemId() == R.id.action_search){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.material_search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(item);
        return true;
    }


    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_viewProducts){
            noResults.setVisibility(View.GONE);
            recyclerViewShow(mDatabase);
            drawerLayout.closeDrawers();
            return true;
        }
        else if(id == R.id.nav_my_fav){
//            final Query query = mDatabase.orderByChild("isFavourite").equalTo("true");
//            query.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(dataSnapshot.getValue() != null){
//                        noResults.setVisibility(View.GONE);
//                        searchGenericResultShow(query);
//                    }
//                    else{
//                        noResults.setVisibility(View.VISIBLE);
//                        noResults.setText("No results found!");
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//            verticalAdapter.cleanup();
            ArrayList<Drug> drugList = new ArrayList<>();
            String query = "SELECT * FROM myFavouriteH;" ;
            Cursor cursor = MainActivity.mySQLiteDb.rawQuery(query,null);
            if(cursor.getCount() == 0){
                recyclerView.setAdapter(null);
                noResults.setVisibility(View.VISIBLE);
                noResults.setText("No favourites found!");
            }
            else{
                while (cursor.moveToNext()){
                    String drud_id = cursor.getString(0);
                    String genName = cursor.getString(1);
                    String comName = cursor.getString(2);
                    String trade = cursor.getString(3);
                    drugList.add(new Drug(trade,genName,drud_id,comName));
                }
                verticalAdapter = null;
                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), drugList, 0);
                recyclerView.setAdapter(customAdapter);
            }
            drawerLayout.closeDrawers();
            return true;
        }
        else if(id == R.id.nav_about){
            Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(aboutIntent);
            drawerLayout.closeDrawers();
            return true;
        }
        else if(id == R.id.nav_contact){
            Intent contactIntent = new Intent(MainActivity.this, ContactActivity.class);
            startActivity(contactIntent);
            drawerLayout.closeDrawers();
            return true;
        }
        drawerLayout.closeDrawers();
        return false;
    }

    public static class UpperViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public UpperViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTradeName(String tradeName){
            TextView tradeTextView = mView.findViewById(R.id.trade_name_id);
            tradeTextView.setText(tradeName);
        }
        public void setGenericName(String genericName){
            TextView genTextView = mView.findViewById(R.id.generic_name_id);
            genTextView.setText(genericName);
        }
        public void setImage(Context context,String image){
            ImageView imageView = mView.findViewById(R.id.drug_image_id);
            final Context ctx = context;
            final String img = image;
            final ImageView imgView = imageView;
            //Picasso.with(context).load(image).into(imageView);
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(img).into(imgView);
                }
            });
        }
    }




    @Override
    public void onBackPressed() {
        if(materialSearchView.isSearchOpen()){
            materialSearchView.closeSearch();
        }
        else{
            super.onBackPressed();
            this.finish();
        }
    }
}
