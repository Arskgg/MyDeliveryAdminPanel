package com.arskgg.mydeliveryadminpanel;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.arskgg.mydeliveryadminpanel.Common.Common;
import com.arskgg.mydeliveryadminpanel.Interface.ItemClickListener;
import com.arskgg.mydeliveryadminpanel.Model.Request;
import com.arskgg.mydeliveryadminpanel.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerOrder;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requestTable;

    FirebaseRecyclerAdapter adapter;

    MaterialSpinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        //Firebase
        database = FirebaseDatabase.getInstance();
        requestTable = database.getReference("Request");

        recyclerOrder = findViewById(R.id.recyclerOrder);
        recyclerOrder.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerOrder.setLayoutManager(layoutManager);

        loadOrders(Common.currentUser.getPhone());

    }

    private void loadOrders(String phone) {

        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(requestTable, Request.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_item, parent, false);

                return new OrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(OrderViewHolder holder, final int position, final Request model) {

                //do binding stuff
                holder.orderId.setText(adapter.getRef(position).getKey());
                holder.orderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                holder.orderPhone.setText(model.getPhone());
                holder.orderAddress.setText(model.getAddress());

                holder.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(position).getKey(), (Request) adapter.getItem(position));
                    }
                });

                holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey());
                    }
                });

                holder.detailBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Common.currentRequest = model;
                        Intent detailIntent = new Intent(getApplicationContext(), OrderDetail.class);
                        detailIntent.putExtra("orderId", adapter.getRef(position).getKey());
                        startActivity(detailIntent);
                    }
                });


                holder.directionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Common.currentRequest = model;
                        Intent mapIntent = new Intent(getApplicationContext(), TrackingOrder.class);
                        startActivity(mapIntent);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        recyclerOrder.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showUpdateDialog(final String key, final Request item) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(OrderStatus.this);
        dialog.setTitle("Update dialog");
        dialog.setMessage("Please choose status");

        LayoutInflater inflater = this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.update_order_dialog, null);

        spinner = view.findViewById(R.id.spinnerStatus);
        spinner.setItems("Placed", "On my way", "Shipped");

        dialog.setView(view);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requestTable.child(key).setValue(item);

                adapter.notifyDataSetChanged();

               dialog.dismiss();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void deleteOrder(String key) {
        requestTable.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }


}
