package com.arskgg.mydeliveryadminpanel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arskgg.mydeliveryadminpanel.Common.Common;
import com.arskgg.mydeliveryadminpanel.Model.Order;
import com.arskgg.mydeliveryadminpanel.ViewHolder.OrderDetailAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderDetail extends AppCompatActivity {

    TextView orderId, orderPhone, orderAddress, orderTotalPrice, orderComment;

    RecyclerView recyclerOrderDetail;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requestTable;

    String orderIdValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = findViewById(R.id.orderId);
        orderPhone = findViewById(R.id.orderPhone);
        orderAddress = findViewById(R.id.orderAddress);
        orderTotalPrice = findViewById(R.id.orderTotalPrice);
        orderComment = findViewById(R.id.orderComment);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requestTable = database.getReference("Request");

        recyclerOrderDetail = findViewById(R.id.recyclerOrderDetail);
        recyclerOrderDetail.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerOrderDetail.setLayoutManager(layoutManager);


        if (getIntent() != null)
            orderIdValue = getIntent().getStringExtra("orderId");


        orderId.setText(orderIdValue);
        orderPhone.setText(Common.currentRequest.getPhone());
        orderAddress.setText(Common.currentRequest.getAddress());
        orderTotalPrice.setText(Common.currentRequest.getTotalPrice());
        orderComment.setText(Common.currentRequest.getComment());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getOrderList());
        adapter.notifyDataSetChanged();
        recyclerOrderDetail.setAdapter(adapter);


    }
}
