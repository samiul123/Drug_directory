package com.example.samiu.drug_directory;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.samiu.drug_directory.MainActivity.deviceToken;
import static com.example.samiu.drug_directory.MainActivity.favList;
import static com.example.samiu.drug_directory.MainActivity.mDialogue;
import static com.example.samiu.drug_directory.MainActivity.mySQLiteDb;

/**
 * Created by samiu on 11/19/2017.
 */

public class VerticalAdapter extends FirebaseRecyclerAdapter<Drug,VerticalAdapter.DrugViewHolder> {

    public Context context;
    public DatabaseReference dataRef;
    public Query ref;
    private int flag;
    public static ArrayList<Drug> productList;



    public VerticalAdapter(Class<Drug> modelClass,
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


    public VerticalAdapter(Class<Drug> modelClass,
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
    protected void populateViewHolder(final DrugViewHolder viewHolder, final Drug model, int position) {
        //String idd = getRef(position).getKey();

        viewHolder.setTradeName(model.getTradeName());
        viewHolder.setGenericName(model.getGenericName());
        viewHolder.setImage(this.context, model.getImage());
        viewHolder.setCompanyName(model.getCompanyName());
        mDialogue.dismiss();

        if(MainActivity.favList.contains(model.getId())){
            //viewHolder.setLike(0);
            viewHolder.likeImg.setImageResource(R.drawable.ic_action_favo);
        }
        else{
            viewHolder.likeImg.setImageResource(R.drawable.ic_action_thumbs);
        }
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "ID: " + model.getId(), Toast.LENGTH_SHORT).show();
                Intent detailIntent = new Intent(context, HomeActivity.class);
                detailIntent.putExtra("drug_id", model.getId());
                detailIntent.putExtra("genericName",model.getGenericName());
                detailIntent.putExtra("tradeName",model.getTradeName());
                context.startActivity(detailIntent);
            }
        });

        viewHolder.likeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id1 = model.getId();
                //Toast.makeText(context, id1, Toast.LENGTH_SHORT).show();
                //DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("drug_table").child(id1);
                //Toast.makeText(context, id1, Toast.LENGTH_SHORT).show();
                if(favList.contains(id1)) {
                    favList.remove(id1);
                    String sql = "DELETE FROM myFavouriteH where id='" + id1 + "'";
                    mySQLiteDb.execSQL(sql);
//                    DatabaseReference fav = FirebaseDatabase.getInstance().getReference().child("Fav_data")
//                            .child(deviceToken);
//                    fav.child(id1).setValue(null);
                    viewHolder.likeImg.setImageResource(R.drawable.ic_action_thumbs);
                    //viewHolder.setLike(1);
//                    mData.child("isFavourite").setValue("false");
//                    mData.child("gen_isFav").setValue(model.getGenericName() + "_false");
                    //Toast.makeText(context, id1+"removed", Toast.LENGTH_SHORT).show();
                }
                else{
                    favList.add(id1);
                    String sql = "INSERT INTO myFavouriteH VALUES('" + id1+ "'," + "'" + model.getGenericName()+"'," + "'" + model.getCompanyName()+"'," + "'" + model.getTradeName()+"')";
                    mySQLiteDb.execSQL(sql);
//                    DatabaseReference fav = FirebaseDatabase.getInstance().getReference().child("Fav_data")
//                            .child(deviceToken).child(id1);
//                    fav.child("genericName").setValue(model.getGenericName());
//                    fav.child("tradeName").setValue(model.getTradeName());
//                    fav.child("companyName").setValue(model.getCompanyName());
//                    fav.child("image").setValue(model.getImage());
//                    fav.child("drug_id").setValue(id1);
                    viewHolder.likeImg.setImageResource(R.drawable.ic_action_favo);
                    //viewHolder.setLike(0);
//                    mData.child("isFavourite").setValue("true");
                    //fav.child("gen_isFav").setValue(model.getGenericName() + "_true");
                    //Toast.makeText(context, id1+"added", Toast.LENGTH_SHORT).show();
                }
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
        ImageView likeImg;
        public DrugViewHolder(View itemView) {
            super(itemView);
            mView = itemView;


            likeImg = mView.findViewById(R.id.like_id);


            /*horizontalView = mView.findViewById(R.id.horizontal_list);
            horizontalView.setHasFixedSize(true);
            horizontalView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            horizontalAdapter = new HorizontalAdapter(Drug.class,
                    R.layout.durg_list_row,
                    HorizontalAdapter.DrugViewHolder.class,
                    databaseReference,itemView.getContext(),1,productList);
            horizontalView.setAdapter(horizontalAdapter);*/
        }

        public void setTradeName(String tradeName){
            TextView tradeTextView = mView.findViewById(R.id.trade_name_id);
            tradeTextView.setText(tradeName);
        }
        public void setGenericName(String genericName){
            TextView genTextView = mView.findViewById(R.id.generic_name_id);
            genTextView.setText(genericName);
        }
        public void setCompanyName(String companyName){
            TextView comTextView = mView.findViewById(R.id.company_name_id);
            comTextView.setText(companyName);
        }
        public void setLike(int fav){
            if(fav == 1){
                ImageView likeImg = mView.findViewById(R.id.like_id);
                likeImg.setImageResource(R.drawable.ic_action_thumbs);
            }
            else{
                ImageView likeImg = mView.findViewById(R.id.like_id);
                likeImg.setImageResource(R.drawable.ic_action_favo);
            }

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
