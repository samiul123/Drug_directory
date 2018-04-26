package com.example.samiu.drug_directory;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    String drug_id;
    private DatabaseReference mDatabase;
    ImageView detail_imageView;
    TextView detail_tradeName, detail_genericName, detail_indications, detail_dosage,detail_companyName,
    detail_source, detail_entry;
    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_detail, container, false);
        detail_imageView = view.findViewById(R.id.detail_imageView_ID);
        detail_tradeName = view.findViewById(R.id.deatail_tradeName_id);
        detail_genericName = view.findViewById(R.id.detail_genericName_id);
        detail_indications = view.findViewById(R.id.detail_indications_id);
        detail_dosage = view.findViewById(R.id.dosage_iD);
        detail_companyName = view.findViewById(R.id.detail_company_name_id);
        detail_source = view.findViewById(R.id.source_iD);
//        detail_entry = view.findViewById(R.id.entryDate_iD);

        drug_id = this.getArguments().getString("drug_id");

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
                    else if(key.equals("source")){
                        detail_source.setText(value);
                        Linkify.addLinks(detail_source, Linkify.WEB_URLS);
                    }

//                    else if(key.equals("entryDate")){
//                        detail_entry.setText(value);
//                    }

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
                        Picasso.with(view.getContext()).load(value).networkPolicy(NetworkPolicy.OFFLINE).into(detail_imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(view.getContext()).load(img).into(imgView);
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
        return view;
    }
}
