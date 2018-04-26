package com.example.samiu.drug_directory;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalculateFragment extends Fragment {

    EditText height, weight, age;
    Button calculateBtn;
    String genericNAme;
    DatabaseReference dataTable;
    String doseValue;
    TextView result, heading, ageTextView, heightTextView, weightTextView;
    double resultV;
    String unitOfresult;

    public CalculateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calculate, container, false);
        height = view.findViewById(R.id.heightId);
        weight = view.findViewById(R.id.weightId);
        age = view.findViewById(R.id.ageId);
        calculateBtn = view.findViewById(R.id.calculateButtonID);
        result = view.findViewById(R.id.resultId);
        genericNAme = this.getArguments().getString("genericName");
        heading = view.findViewById(R.id.gName_id);
        ageTextView = view.findViewById(R.id.ageTextView_id);
        heightTextView = view.findViewById(R.id.heightTextView_id);
        weightTextView = view.findViewById(R.id.weightTextView_id);
        String hd = "Dose calculation for " + genericNAme.toUpperCase();
        heading.setText(hd);
        System.out.println("in calculate: " + genericNAme);
        dataTable = FirebaseDatabase.getInstance().getReference().child("data_table");
        dataTable.keepSynced(true);
        age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    String str = "";
                    ageTextView.setText(str);
                }
                else{
                    String str = "Age(yrs)";
                    ageTextView.setText(str);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    String str = "";
                    heightTextView.setText(str);
                }
                else{
                    String str = "Height(inch)";
                    heightTextView.setText(str);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    String str = "";
                    weightTextView.setText(str);
                }else{
                    String str = "Weight(kg)";
                    weightTextView.setText(str);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String page = age.getText().toString();
                String pweight = weight.getText().toString();
                String pheight = height.getText().toString();
                if(!TextUtils.isEmpty(page) && !TextUtils.isEmpty(pweight) && !TextUtils.isEmpty(pheight)){
                    if(!isNumeric(page) && !isNumeric(pweight) && !isNumeric(pheight)){
                        Toast.makeText(getContext(), "Fill the field with number only", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        double mage = Double.parseDouble(page);
                        double mweight = Double.parseDouble(pweight);
                        double mheight = Double.parseDouble(pheight);
                        mheight = mheight*2.54;

                        final double bsa = Math.sqrt(mweight*mheight/3600);
                        dataTable.child(genericNAme).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot child: dataSnapshot.getChildren()){
                                    String key = child.getKey();
                                    System.out.println("in cal: " + key);
                                    if(key.equals("doses")){
                                        doseValue = (String) child.getValue();
                                        resultV = Math.round(bsa* Double.parseDouble(doseValue)/1.7);

                                        dataTable.child(genericNAme).child("unit").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                unitOfresult = (String) dataSnapshot.getValue();
                                                String strRslt = resultV + " " + unitOfresult;
                                                result.setText(strRslt);
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

                    }
                }
                else{
                    Toast.makeText(getContext(), "Fill up all fields", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return view;
    }

    private boolean isNumeric(String str) {
        try{
            double d = Double.parseDouble(str);
        }catch (NumberFormatException nfe){
            return false;
        }
        return true;
    }

}
