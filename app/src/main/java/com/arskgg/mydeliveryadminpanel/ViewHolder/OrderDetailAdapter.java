package com.arskgg.mydeliveryadminpanel.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arskgg.mydeliveryadminpanel.Model.Order;
import com.arskgg.mydeliveryadminpanel.R;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailViewHolder>{

    List<Order> orders;

    public OrderDetailAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_detail_item, viewGroup, false);

        return new OrderDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder orderDetailViewHolder, int i) {

        Order order = orders.get(i);

        orderDetailViewHolder.name.setText(String.format("Name: %s",order.getProductName()));
        orderDetailViewHolder.quantity.setText(String.format("Quantity: %s",order.getQuantity()));
        orderDetailViewHolder.price.setText(String.format("Price: %s",order.getPrice()));
        orderDetailViewHolder.discount.setText(String.format("Discount: %s",order.getDiscount()));

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}

class OrderDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView name, quantity, price, discount;

    public OrderDetailViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.productName);
        quantity = itemView.findViewById(R.id.productQuantity);
        price = itemView.findViewById(R.id.productPrice);
        discount = itemView.findViewById(R.id.productDiscount);
    }

    @Override
    public void onClick(View v) {

    }
}