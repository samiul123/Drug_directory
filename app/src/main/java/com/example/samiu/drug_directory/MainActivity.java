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
    private RecyclerView recyclerView;
    private RecyclerView horizontal_recycler_view;
    public static ProgressDialog mDialogue;
    ArrayList<String> tradeNames = new ArrayList<>();
    ArrayList<String> genericNames = new ArrayList<>();
    public static ArrayList<Drug> productList = new ArrayList<>();
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle toogle;
    public NavigationView navView;
    Toolbar toolbar;
    private List<UpperView> data;
    VerticalAdapter verticalAdapter;
    ArrayList<String> names = new ArrayList<>();
    MaterialSearchView materialSearchView;
    UpperHorizontalAdapter upperHorizontalAdapter;
    public static SQLiteDatabase mySQLiteDb;
    public static ArrayList<String> favList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        //recyclerView
        recyclerView = findViewById(R.id.drug_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        /*horizontal_recycler_view = findViewById(R.id.upper_recycler_view);
        horizontal_recycler_view.setHasFixedSize(true);
        horizontal_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        data = fill_with_data();
        upperHorizontalAdapter = new UpperHorizontalAdapter(data, getApplicationContext());
        horizontal_recycler_view.setAdapter(upperHorizontalAdapter);
*/

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
        mySQLiteDb.execSQL("CREATE TABLE IF NOT EXISTS myFavourite(id VARCHAR);");
        //mySQLiteDb.execSQL("INSERT INTO myFavourite values('asd')");
        Cursor result = mySQLiteDb.rawQuery("SELECT * FROM myFavourite", null);
        favList = new ArrayList<>();
        while(result.moveToNext()){
            String id = result.getString(0);
            //Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
            favList.add(id);
        }


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

    public List<UpperView> fill_with_data() {

        List<UpperView> data = new ArrayList<>();
        data.add(new UpperView( R.drawable.a));
        data.add(new UpperView( R.drawable.b));
        data.add(new UpperView( R.drawable.c));
        data.add(new UpperView( R.drawable.d));
        data.add(new UpperView( R.drawable.e));
        return data;
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
        verticalAdapter.cleanup();
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
                            Query query1 = searchResultsRef.orderByChild("genericName").equalTo(gen_name);
                            searchGenericResultShow(query1);
                        }
                    }
                }

                else if(genericNames.contains(query)){
                    Query query1 = searchResultsRef.orderByChild("genericName").equalTo(query);
                    searchGenericResultShow(query1);
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
        //mDialogue.setMessage("Please wait...");
        //mDialogue.show();
        recyclerViewShow(mDatabase);
    }

    public void recyclerViewShow(final DatabaseReference ref){

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
        }
    }
}
