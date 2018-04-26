package com.example.samiu.drug_directory;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.samiu.drug_directory.MainActivity.mDialogue;

/**
 * Created by samiu on 11/19/2017.
 */

public class HorizontalAdapter extends FirebaseRecyclerAdapter<Drug, HorizontalAdapter.DrugViewHolder> {

    public Context context;
    public DatabaseReference dataRef;
    public Query ref;
    private int flag;
    private ArrayList<Drug> productList;

    public HorizontalAdapter(Class<Drug> modelClass,
                             int modelLayout,
                             Class<DrugViewHolder> viewHolderClass,
                             Query ref,
                             Context context,
                             int flag) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        this.ref = ref;
        this.flag = flag;
    }


    public HorizontalAdapter(Class<Drug> modelClass,
                             int modelLayout,
                             Class<DrugViewHolder> viewHolderClass,
                             DatabaseReference ref,
                             Context context,
                             int flag,
                             ArrayList<Drug> productList) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        this.dataRef = ref;
        this.flag = flag;
        this.productList = productList;
    }


    @Override
    protected void populateViewHolder(DrugViewHolder viewHolder, final Drug model, int position) {
        viewHolder.setTradeName(model.getTradeName());
        viewHolder.setGenericName(model.getGenericName());
        viewHolder.setImage(this.context, model.getImage());

        mDialogue.dismiss();
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "ID: " + model.getId(), Toast.LENGTH_SHORT).show();
                Intent detailIntent = new Intent(context, DetailsActivity.class);
                detailIntent.putExtra("drug_id", model.getId());
                context.startActivity(detailIntent);
            }
        });


        Log.d("Finish", "dismiss");
        if(flag == 1){
            dataRef.orderByChild("tradeName").equalTo(model.getTradeName()).addValueEventListener(new ValueEventListener() {
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
            productList.add(model);
        }
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
}
