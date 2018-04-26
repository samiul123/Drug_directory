package com.example.samiu.drug_directory;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SimilarFavFragment extends Fragment {

    public static RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    VerticalAdapter verticalAdapter;
    String drug_id;
    String genericName, tradeName;
    public static TextView noResultsFound;
    public SimilarFavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_similar_fav, container, false);
        recyclerView = view.findViewById(R.id.drug_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        noResultsFound = view.findViewById(R.id.noResultsId);


        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
        marginLayoutParams.setMargins(0, 80, 0, 0);
        recyclerView.setLayoutParams(marginLayoutParams);


        //Configuring fireBase Database
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("drug_table");
//        final DatabaseReference fav = FirebaseDatabase.getInstance().getReference().child("Fav_data");
//        mDatabase.keepSynced(true);
//
        drug_id = this.getArguments().getString("drug_id");
        genericName = this.getArguments().getString("genericName");
        tradeName = this.getArguments().getString("tradeName");
//        System.out.println("in sim_fav: " + genericName);
//        fav.child(MainActivity.deviceToken).child(drug_id).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    String value = String.valueOf(child.getValue());
//                    String key = child.getKey();
//                    if(key.equals("genericName")){
//                        final Query query1 = fav.orderByChild("genericName").equalTo(genericName);
//                        query1.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if(dataSnapshot.getValue() != null){
//                                    noResultsFound.setVisibility(View.GONE);
//                                    searchGenericResultShow(query1);
//                                }
//                                else{
//                                    noResultsFound.setVisibility(View.VISIBLE);
//                                    noResultsFound.setText("No similar favourites found!");
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
        ArrayList<Drug> drugList = new ArrayList<>();
        String query = "SELECT * FROM myFavouriteH WHERE genericName = '" + genericName + "';";
        Cursor cursor = MainActivity.mySQLiteDb.rawQuery(query,null);
        if(cursor.getCount() == 0){
            recyclerView.setAdapter(null);
            noResultsFound.setVisibility(View.VISIBLE);
            noResultsFound.setText("No similar favourites found!");
        }
        else{
            while (cursor.moveToNext()){
                String id = cursor.getString(0);
                String genName = cursor.getString(1);
                String comName = cursor.getString(2);
                String trade = cursor.getString(3);
                drugList.add(new Drug(trade,genName,id,comName));
            }
            CustomAdapter customAdapter = new CustomAdapter(view.getContext(), drugList, 1);
            recyclerView.setAdapter(customAdapter);
        }


        return view;
    }
//    public void searchGenericResultShow(Query query){
//        if(verticalAdapter != null){
//            verticalAdapter.cleanup();
//        }
//        verticalAdapter = new VerticalAdapter(Drug.class,
//                R.layout.durg_list_row,
//                VerticalAdapter.DrugViewHolder.class,
//                query,this.getContext(), 0);
//        recyclerView.setAdapter(verticalAdapter);
//    }

}
