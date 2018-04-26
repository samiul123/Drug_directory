package com.example.samiu.drug_directory;

/**
 * Created by samiu on 1/11/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.samiu.drug_directory.MainActivity.favList;
import static com.example.samiu.drug_directory.MainActivity.mySQLiteDb;

/**
 * Created by samiu on 1/11/2018.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    ArrayList<Drug> drugs;
    Context context;
    int flag;
    String image;
    String tradeNameStr;
    public CustomAdapter(Context context, ArrayList<Drug> drugs, int flag) {
        this.context = context;
        this.drugs = drugs;
        this.flag = flag;
    }
    public CustomAdapter(Context context, ArrayList<Drug> drugs, int flag, String tradeNameStr){
        this.context = context;
        this.drugs = drugs;
        this.flag = flag;
        this.tradeNameStr = tradeNameStr;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.durg_list_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

//    /**
//     * Called by RecyclerView to display the data at the specified position. This method should
//     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
//     * position.
//     * <p>
//     * Note that unlike {@link ListView}, RecyclerView will not call this method
//     * again if the position of the item changes in the data set unless the item itself is
//     * invalidated or the new position cannot be determined. For this reason, you should only
//     * use the <code>position</code> parameter while acquiring the related data item inside
//     * this method and should not keep a copy of it. If you need the position of an item later
//     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
//     * have the updated adapter position.
//     * <p>
//     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
//     * handle efficient partial bind.
//     *
//     * @param holder   The ViewHolder which should be updated to represent the contents of the
//     *                 item at the given position in the data set.
//     * @param position The position of the item within the adapter's data set.
//     */


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // set the data in items
        holder.tradeName.setText(drugs.get(position).getTradeName());
        holder.genericName.setText(drugs.get(position).getGenericName());
        holder.companyName.setText(drugs.get(position).getCompanyName());
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("drug_table")
                .child(drugs.get(position).getId());
        mData.child("image").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.setImage(context, (String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(MainActivity.favList.contains(drugs.get(position).getId())){
            holder.like.setImageResource(R.drawable.ic_action_favo);
        }
//        else{
//            holder.like.setImageResource(R.drawable.ic_action_thumbs);
//        }
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                //Toast.makeText(context, personNames.get(position), Toast.LENGTH_SHORT).show();
                Intent detailIntent = new Intent(context, HomeActivity.class);
                detailIntent.putExtra("drug_id", drugs.get(position).getId());
                detailIntent.putExtra("genericName",drugs.get(position).getGenericName());
                context.startActivity(detailIntent);
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = drugs.get(position).getId();
                favList.remove(drugs.get(position).getId());
                String sql = "DELETE FROM myFavouriteH where id='" + id + "'";
                mySQLiteDb.execSQL(sql);
//                    DatabaseReference fav = FirebaseDatabase.getInstance().getReference().child("Fav_data")
//                            .child(deviceToken);
//                    fav.child(id1).setValue(null);
                holder.like.setImageResource(R.drawable.ic_action_thumbs);
//                ArrayList<Drug> drugList = new ArrayList<>();
//                String query = "SELECT * FROM myFavouriteH;" ;
//                Cursor cursor = MainActivity.mySQLiteDb.rawQuery(query,null);
//                while (cursor.moveToNext()){
//                    String drud_id = cursor.getString(0);
//                    String genName = cursor.getString(1);
//                    String comName = cursor.getString(2);
//                    String trade = cursor.getString(3);
//                    drugList.add(new Drug(trade,genName,drud_id,comName));
//                }
                //verticalAdapter = null;
                if(flag == 0){
                    ArrayList<Drug> drugList = new ArrayList<>();
                    String query = "SELECT * FROM myFavouriteH;" ;
                    Cursor cursor = MainActivity.mySQLiteDb.rawQuery(query,null);
                    while (cursor.moveToNext()){
                        String drud_id = cursor.getString(0);
                        String genName = cursor.getString(1);
                        String comName = cursor.getString(2);
                        String trade = cursor.getString(3);
                        drugList.add(new Drug(trade,genName,drud_id,comName));
                    }
                    if(cursor.getCount() == 0){
                        MainActivity.noResults.setVisibility(View.VISIBLE);
                        MainActivity.noResults.setText("No favourites found!");
                    }
                    CustomAdapter customAdapter = new CustomAdapter(context, drugList, 0);
                    MainActivity.recyclerView.setAdapter(customAdapter);
                }
                else{
                    ArrayList<Drug> drugList = new ArrayList<>();
//                    String query = "SELECT * FROM myFavouriteH WHERE genericName = '" + holder.genericName + "';";
                    String query = "SELECT * FROM myFavouriteH WHERE genericName = '" + holder.genericName.getText() + "';";
                    System.out.println(query);
                    //Toast.makeText(context, query, Toast.LENGTH_SHORT).show();
                    Cursor cursor = MainActivity.mySQLiteDb.rawQuery(query,null);
                    if(cursor.getCount() == 0){
                        SimilarFavFragment.noResultsFound.setVisibility(View.VISIBLE);
                        SimilarFavFragment.noResultsFound.setText("No similar favourites found!");
                    }

                    while (cursor.moveToNext()){
                        String drud_id = cursor.getString(0);
                        String genName = cursor.getString(1);
                        String comName = cursor.getString(2);
                        String trade = cursor.getString(3);
                        drugList.add(new Drug(trade,genName,drud_id,comName));
                    }

                    CustomAdapter customAdapter = new CustomAdapter(context, drugList, 1);
                    SimilarFavFragment.recyclerView.setAdapter(customAdapter);
                }

            }
        });
    }
    @Override
    public int getItemCount() {
        return drugs.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tradeName;// init the item view's
        TextView genericName;
        TextView companyName;
        ImageView like;
        ImageView drug_pic;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            tradeName = itemView.findViewById(R.id.trade_name_id);
            genericName = itemView.findViewById(R.id.generic_name_id);
            companyName = itemView.findViewById(R.id.company_name_id);
            like = itemView.findViewById(R.id.like_id);
            drug_pic = itemView.findViewById(R.id.drug_image_id);
        }

        public void setImage(final Context context, final String image){
            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(drug_pic, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).into(drug_pic);
                }
            });
        }
    }
}