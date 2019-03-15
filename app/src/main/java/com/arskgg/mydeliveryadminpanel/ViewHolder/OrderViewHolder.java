package com.arskgg.mydeliveryadminpanel.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arskgg.mydeliveryadminpanel.Common.Common;
import com.arskgg.mydeliveryadminpanel.Interface.ItemClickListener;
import com.arskgg.mydeliveryadminpanel.R;


public class OrderViewHolder extends RecyclerView.ViewHolder{

    public TextView orderId, orderStatus, orderPhone, orderAddress;
    public Button editBtn, removeBtn, detailBtn, directionBtn;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        orderId = itemView.findViewById(R.id.orderId);
        orderStatus = itemView.findViewById(R.id.orderStatus);
        orderPhone = itemView.findViewById(R.id.orderPhone);
        orderAddress = itemView.findViewById(R.id.orderAddress);

        editBtn = itemView.findViewById(R.id.editBtn);
        removeBtn = itemView.findViewById(R.id.removeBtn);
        detailBtn = itemView.findViewById(R.id.detailBtn);
        directionBtn = itemView.findViewById(R.id.directionBtn);


    }

}
