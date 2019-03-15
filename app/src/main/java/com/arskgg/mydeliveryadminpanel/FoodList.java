package com.arskgg.mydeliveryadminpanel;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.arskgg.mydeliveryadminpanel.Common.Common;
import com.arskgg.mydeliveryadminpanel.Interface.ItemClickListener;
import com.arskgg.mydeliveryadminpanel.Model.Food;
import com.arskgg.mydeliveryadminpanel.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerFood;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference foodTable;

    FirebaseStorage storage;
    StorageReference storageReference;

    Food newFood;
    Uri savedUri;

    //Add new menu Layout for customDialog
    EditText edtName, edtDescription, edtPrice, edtDiscount;
    Button selectBtn, uploadBtn;

    FloatingActionButton fab;
    RelativeLayout rootLayout;

    String categoryId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        //Firebase
        database = FirebaseDatabase.getInstance();
        foodTable = database.getReference("Food");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recyclerFood = findViewById(R.id.recyclerFood);
        recyclerFood.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerFood.setLayoutManager(layoutManager);

        rootLayout = findViewById(R.id.rootFoodListLayout);

        fab = findViewById(R.id.fabAddFood);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddFoodDialog();
            }
        });


        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("categoryId");
        if (!categoryId.isEmpty())
            loadListFood();


    }


    private void loadListFood() {

        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                // Select from table where menuId == categoryId
                .setQuery(foodTable.orderByChild("menuId").equalTo(categoryId), Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.food_item, viewGroup, false);

                return new FoodViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, Food model) {

                holder.foodName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.foodImage);
                final Food clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
//                        Intent foodDetail = new Intent(getApplicationContext(), FoodDetail.class);
//                        //Put element ID which was clicked
//                        foodDetail.putExtra("foodId",adapter.getRef(position).getKey());
//                        startActivity(foodDetail);

                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        recyclerFood.setAdapter(adapter);
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

    private void showAddFoodDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(FoodList.this);
        dialog.setTitle("Add new Food");
        dialog.setMessage("Please fill information");

        LayoutInflater inflater = this.getLayoutInflater();
        View addCustomDialog = inflater.inflate(R.layout.add_item_food_dialog, null);

        edtName = addCustomDialog.findViewById(R.id.edtName);
        edtDescription = addCustomDialog.findViewById(R.id.edtDescription);
        edtPrice = addCustomDialog.findViewById(R.id.edtPrice);
        edtDiscount = addCustomDialog.findViewById(R.id.edtDiscount);

        selectBtn = addCustomDialog.findViewById(R.id.selectBtn);
        uploadBtn = addCustomDialog.findViewById(R.id.uploadBtn);

        dialog.setView(addCustomDialog);
        dialog.setIcon(R.drawable.ic_shopping_cart);

        //Even for buttons
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();  //Let user choose Image from Gallery and save Uri of this image
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(newFood != null){

                    foodTable.push().setValue(newFood);
                    Snackbar.make(rootLayout, "New Food " + newFood.getName()
                            + " has been added", Snackbar.LENGTH_SHORT).show();
                }

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


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {

            savedUri = data.getData();
            selectBtn.setText("Image Selected!");
        }
    }

    private void uploadImage() {

        if (savedUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(savedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressDialog.dismiss();
                    Toast.makeText(FoodList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            newFood = new Food(edtName.getText().toString(),
                                    uri.toString(),
                                    edtDescription.getText().toString(),
                                    edtPrice.getText().toString(),
                                    edtDiscount.getText().toString(),
                                    categoryId);
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                }
            });


        }
    }


    //Update & Delete Food item

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE))
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(), (Food) adapter.getItem(item.getOrder()));

        else if (item.getTitle().equals(Common.DELETE))
            deleteFood(adapter.getRef(item.getOrder()).getKey());

        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {

        foodTable.child(key).removeValue();
    }

    private void showUpdateFoodDialog(final String key, final Food item) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(FoodList.this);
        dialog.setTitle("Edit Food");
        dialog.setMessage("Please fill information");

        LayoutInflater inflater = this.getLayoutInflater();
        View addCustomDialog = inflater.inflate(R.layout.add_item_food_dialog, null);

        edtName = addCustomDialog.findViewById(R.id.edtName);
        edtDescription = addCustomDialog.findViewById(R.id.edtDescription);
        edtPrice = addCustomDialog.findViewById(R.id.edtPrice);
        edtDiscount = addCustomDialog.findViewById(R.id.edtDiscount);

        selectBtn = addCustomDialog.findViewById(R.id.selectBtn);
        uploadBtn = addCustomDialog.findViewById(R.id.uploadBtn);

        dialog.setView(addCustomDialog);
        dialog.setIcon(R.drawable.ic_shopping_cart);

        edtName.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtPrice.setText(item.getPrice());
        edtDiscount.setText(item.getDiscount());

        //Even for buttons
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();  //Let user choose Image from Gallery and save Uri of this image
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Update information
                item.setName(edtName.getText().toString());
                item.setDescription(edtDescription.getText().toString());
                item.setPrice(edtPrice.getText().toString());
                item.setDiscount(edtDiscount.getText().toString());


                foodTable.child(key).setValue(item);

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

    private void changeImage(final Food item) {

        if (savedUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(savedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(FoodList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            item.setImage(uri.toString());
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                }
            });


        }



    }


}
