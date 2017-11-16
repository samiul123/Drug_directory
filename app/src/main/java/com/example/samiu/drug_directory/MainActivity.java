package com.example.samiu.drug_directory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.ViewFlipper;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    private DatabaseReference mDatabase;
    private DatabaseReference suggestionRef;
    private DatabaseReference searchResultsRef;
    private RecyclerView recyclerView;
    private ProgressDialog mDialogue;
    ArrayList<String> tradeNames = new ArrayList<>();
    ArrayList<String> genericNames = new ArrayList<>();
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle toogle;
    public NavigationView navView;
    Toolbar toolbar;
    ArrayList<String> names = new ArrayList<>();
    MaterialSearchView materialSearchView;
    FirebaseRecyclerAdapter<Drug, DrugViewHolder> fireBaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        //recyclerView
        recyclerView = findViewById(R.id.drug_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
        marginLayoutParams.setMargins(0, 110, 0, 0);
        recyclerView.setLayoutParams(marginLayoutParams);

        //Configuring fireBase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Drugs");
        suggestionRef = FirebaseDatabase.getInstance().getReference().child("Drugs");
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
        searchResultsRef = FirebaseDatabase.getInstance().getReference().child("Drugs");

        //progress
        mDialogue = new ProgressDialog(this);

        materialSearchView = findViewById(R.id.search_view_id);

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
            names.add((String) singleName.get("genericName"));
            genericNames.add((String) singleName.get("genericName"));
        }
        String[] suggestions = names.toArray(new String[names.size()]);
        materialSearchView.setSuggestions(suggestions);
    }


    private void searchViewCode() {
        materialSearchView.setEllipsize(true);
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {

                if(tradeNames.contains(query)){
                    final String[] id = new String[1];

                    suggestionRef.orderByChild("tradeName").equalTo(query).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot child: dataSnapshot.getChildren()){
                                id[0] = child.getKey();
                                Toast.makeText(getApplicationContext(),query + " " + id[0], Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

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

    @Override
    protected void onStart() {
        super.onStart();
        mDialogue.setMessage("Please wait...");
        mDialogue.show();
        recyclerViewShow(mDatabase);
    }

    public void recyclerViewShow(final DatabaseReference ref){
        fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Drug, DrugViewHolder>(
                Drug.class,
                R.layout.durg_list_row,
                DrugViewHolder.class,
                ref
        ) {

            @Override
            protected void populateViewHolder(DrugViewHolder viewHolder, final Drug model, int position) {
                viewHolder.setTradeName(model.getTradeName());
                viewHolder.setGenericName(model.getGenericName());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                mDialogue.dismiss();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "ID: " + model.getId(), Toast.LENGTH_SHORT).show();
                        Intent detailIntent = new Intent(MainActivity.this, DetailsActivity.class);
                        detailIntent.putExtra("drug_id", model.getId());
                        startActivity(detailIntent);
                    }
                });


                Log.d("Finish", "dismiss");
                ref.orderByChild("tradeName").equalTo(model.getTradeName()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Drug drug = dataSnapshot.getValue(Drug.class);
//                        System.out.println("DRUG:" + drug + " KEY:" + dataSnapshot.getKey() + " Children: " + dataSnapshot.getChildrenCount());
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            System.out.println("Child key:" + child.getKey());
                            model.setId(child.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        recyclerView.setAdapter(fireBaseRecyclerAdapter);
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
        if(id == R.id.db_id ){
            Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawers();
            return true;
        }
        return false;
    }




    public static class DrugViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public DrugViewHolder(View itemView) {
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
