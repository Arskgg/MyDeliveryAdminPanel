package com.arskgg.mydeliveryadminpanel.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arskgg.mydeliveryadminpanel.R;

public class ShipperViewHolder extends RecyclerView.ViewHolder {

    public TextView shipperName, shipperPhone;
    public Button editBtn, removeBtn;


    public ShipperViewHolder(@NonNull View itemView) {
        super(itemView);

        shipperName = itemView.findViewById(R.id.shipperName);
        shipperPhone = itemView.findViewById(R.id.shipperPhone);
        editBtn = itemView.findViewById(R.id.editBtn);
        removeBtn = itemView.findViewById(R.id.removeBtn);
    }
}
