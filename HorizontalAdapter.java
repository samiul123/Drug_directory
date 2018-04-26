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

/**
 * Created by samiu on 11/19/2017.
 */

public class HorizontalAdapter extends FirebaseRecyclerAdapter {

    public Context context;
    private Class aClass;
    public DatabaseReference dataRef;
    public Query ref;
    public Drug drug;
    public HorizontalAdapter(Class modelClass, int modelLayout, Class viewHolderClass, Query ref, Context context, Class aClass) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        this.aClass = aClass;
        this.ref = ref;
    }


    public HorizontalAdapter(Class modelClass, int modelLayout, Class viewHolderClass, DatabaseReference ref, Context context, Class aClass) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        this.aClass = aClass;
        this.dataRef = ref;
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param viewHolder The view to populate
     * @param model      The object containing the data used to populate the view
     * @param position   The position in the list of the view being populated
     */
    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
        viewHolder = new DrugViewHolder();
        viewHolder.setTradeName(model.getTradeName());
        viewHolder.setGenericName(model.getGenericName());
        viewHolder.setImage(this.context, model.getImage());


        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(this, "ID: " + model.getId(), Toast.LENGTH_SHORT).show();
                Intent detailIntent = new Intent(MainActivity.this, DetailsActivity.class);
                detailIntent.putExtra("drug_id", model.getId());
                startActivity(detailIntent);
            }
        });


        Log.d("Finish", "dismiss");
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
