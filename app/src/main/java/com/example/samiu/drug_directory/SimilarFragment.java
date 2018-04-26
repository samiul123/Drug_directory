package com.example.samiu.drug_directory;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class SimilarFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    VerticalAdapter verticalAdapter;
    TextView noResults;
    String drug_id;
    public SimilarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_similar, container, false);
        recyclerView = view.findViewById(R.id.drug_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        noResults = view.findViewById(R.id.noResultId);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
        marginLayoutParams.setMargins(0, 80, 0, 0);
        recyclerView.setLayoutParams(marginLayoutParams);

        //Configuring fireBase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("drug_table");
        mDatabase.keepSynced(true);

        drug_id = this.getArguments().getString("drug_id");

        mDatabase.child(drug_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String value = String.valueOf(child.getValue());
                    String key = child.getKey();
                    if(key.equals("genericName")){
                        final Query query1 = mDatabase.orderByChild("genericName").equalTo(value);
                        query1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() != null){
                                    noResults.setVisibility(View.GONE);
                                    searchGenericResultShow(query1);
                                }
                                else{
                                    noResults.setVisibility(View.VISIBLE);
                                    noResults.setText("No similar medicines found!");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
    public void searchGenericResultShow(Query query){
        if(verticalAdapter != null){
            verticalAdapter.cleanup();
        }
        verticalAdapter = new VerticalAdapter(Drug.class,
                R.layout.durg_list_row,
                VerticalAdapter.DrugViewHolder.class,
                query,this.getContext(), 0);
        recyclerView.setAdapter(verticalAdapter);
    }

}
