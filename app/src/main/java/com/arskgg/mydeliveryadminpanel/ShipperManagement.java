package com.arskgg.mydeliveryadminpanel;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.arskgg.mydeliveryadminpanel.Model.Shippers;
import com.arskgg.mydeliveryadminpanel.ViewHolder.ShipperViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ShipperManagement extends AppCompatActivity {

    RecyclerView recyclerShippers;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference shippersTable;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_management);

        fab = findViewById(R.id.addShipperFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddShipperDialog();
            }
        });

        //Firebase
        database = FirebaseDatabase.getInstance();
        shippersTable = database.getReference("Shippers");

        recyclerShippers = findViewById(R.id.recyclerShippers);
        recyclerShippers.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerShippers.setLayoutManager(layoutManager);

        loadShippersList();

    }

    private void loadShippersList() {

        FirebaseRecyclerOptions<Shippers> options = new FirebaseRecyclerOptions.Builder<Shippers>()
                .setQuery(shippersTable, Shippers.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Shippers, ShipperViewHolder>(options) {
            @NonNull
            @Override
            public ShipperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.shipper_item, viewGroup, false);

                return new ShipperViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull ShipperViewHolder holder, final int position, @NonNull final Shippers model) {

                holder.shipperName.setText(model.getName());
                holder.shipperPhone.setText(model.getPhone());

                holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeShipperDialog(position);
                    }
                });

                holder.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editShipperDialog(model, adapter.getRef(position).getKey());
                    }
                });
            }
        };

        recyclerShippers.setAdapter(adapter);
    }

    private void editShipperDialog(final Shippers shipper, final String key) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(ShipperManagement.this)
                .setTitle("Edit shipper")
                .setMessage("Edit shipper information");

        LayoutInflater inflater = this.getLayoutInflater();
        View myView = inflater.inflate(R.layout.add_shipper_dialog,null);

        final EditText edtName = myView.findViewById(R.id.edtName);
        final EditText edtPhone = myView.findViewById(R.id.edtPhone);
        final EditText edtPassword = myView.findViewById(R.id.edtPassword);

        edtName.setText(shipper.getName());
        edtPhone.setText(shipper.getPhone());
        edtPassword.setText(shipper.getPassword());

        dialog.setView(myView);
        dialog.setIcon(R.drawable.ic_add_shipper);

        dialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Map<String, Object> update = new HashMap<>();
                update.put("name",edtName.getText().toString().trim());
                update.put("phone",edtPhone.getText().toString().trim());
                update.put("password",edtPassword.getText().toString());

                shippersTable.child(key).removeValue();

                shippersTable.child(edtPhone.getText().toString().trim()).setValue(update)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagement.this, "Shipper was Changed!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void removeShipperDialog(final int position) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(ShipperManagement.this)
                .setTitle("Remove Shipper")
                .setMessage("Are you sure?");

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                shippersTable.child(adapter.getRef(position).getKey()).removeValue();

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showAddShipperDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(ShipperManagement.this)
                .setTitle("Add new shipper")
                .setMessage("Fill shipper information");

        LayoutInflater inflater = this.getLayoutInflater();
        View myView = inflater.inflate(R.layout.add_shipper_dialog,null);

        final EditText edtName = myView.findViewById(R.id.edtName);
        final EditText edtPhone = myView.findViewById(R.id.edtPhone);
        final EditText edtPassword = myView.findViewById(R.id.edtPassword);

        dialog.setView(myView);
        dialog.setIcon(R.drawable.ic_add_shipper);

        dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                shippersTable.child(edtPhone.getText().toString().trim()).setValue(new Shippers(
                                edtName.getText().toString().trim(),
                                edtPhone.getText().toString().trim(),
                                edtPassword.getText().toString()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShipperManagement.this, "Shipper was added!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ShipperManagement.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
}

