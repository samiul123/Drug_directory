package com.example.samiu.drug_directory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    ImageView detail_imageView;
    TextView detail_tradeName, detail_genericName, detail_indications, detail_dosage,detail_companyName;
    String drug_id;
    private DatabaseReference mDatabase;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle toogle;
    public NavigationView navView;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_details);

        detail_imageView = findViewById(R.id.detail_imageView_ID);
        detail_tradeName = findViewById(R.id.deatail_tradeName_id);
        detail_genericName = findViewById(R.id.detail_genericName_id);
        detail_indications = findViewById(R.id.detail_indications_id);
        detail_dosage = findViewById(R.id.dosage_iD);
        detail_companyName = findViewById(R.id.detail_company_name_id);

//        navView = findViewById(R.id.nav_id);
//        navView.setNavigationItemSelectedListener(this);

        toolbar = findViewById(R.id.toolbarID);
        setSupportActionBar(toolbar);

//        drawerLayout = findViewById(R.id.myDrawer);
//        toogle = new ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close);
//        drawerLayout.addDrawerListener(toogle);
//        toogle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent received_intent = getIntent();
        drug_id = received_intent.getStringExtra("drug_id");
        //detail_tradeName.setText(drug_id);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("drug_table");
        mDatabase.keepSynced(true);
        mDatabase.child(drug_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    String value = String.valueOf(child.getValue());
                    String key = child.getKey();
                    if(key.equals("tradeName")){
                        detail_tradeName.setText(value);
                    }

                    else if(key.equals("companyName")){
                        detail_companyName.setText(value);
                    }
                    else if(key.equals("genericName")){
                        detail_genericName.setText(value);
                    }
                    else if(key.equals("image")){
                        final String img = value;
                        final ImageView imgView = detail_imageView;
                        //Picasso.with(context).load(image).into(imageView);
                        Picasso.with(getApplicationContext()).load(value).networkPolicy(NetworkPolicy.OFFLINE).into(detail_imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext()).load(img).into(imgView);
                            }
                        });
                    }
                    else if(key.equals("indication")){
                        /*for(DataSnapshot childOfChild: child.getChildren()){
                            System.out.println(childOfChild.getKey());
                            String valueOfChild = String.valueOf(childOfChild.getValue());
                            detail_indications.append(valueOfChild + "\n");
                        }*/
                        detail_indications.append(value);
                    }
                    else if(key.equals("doses")){
                        /*for(DataSnapshot childOfChild: child.getChildren()){
                            //String keyChild = childOfChild.getKey();
                            String keyChild = "<font color=#224B6C size=10>" + childOfChild.getKey() + "</font>";
                            //detail_dosage.append(keyChild + "\n");
                            detail_dosage.append(Html.fromHtml(keyChild));
                            detail_dosage.append("\n");

                            for(DataSnapshot childOfChildOfChild: childOfChild.getChildren()){
                                String keyChildChild = "<font color=#5991BD>"+ childOfChildOfChild.getKey() + "</font>";
                                detail_dosage.append("\r");
                                detail_dosage.append(Html.fromHtml(keyChildChild));
                                String valueOfChildChild = String.valueOf(childOfChildOfChild.getValue());
                                detail_dosage.append("\r\r" + valueOfChildChild + "\n");
                            }
                        }*/

                            detail_dosage.append(value);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!toogle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
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
//            Intent mainIntent = new Intent(DetailsActivity.this, MainActivity.class);
//            startActivity(mainIntent);
//            return true;
//        }
//        if(id == R.id.nav_viewProducts){
//            drawerLayout.closeDrawers();
//            Intent mainIntent = new Intent(DetailsActivity.this, MainActivity.class);
//            startActivity(mainIntent);
//            return true;
//        }
//
//        return false;
//    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
