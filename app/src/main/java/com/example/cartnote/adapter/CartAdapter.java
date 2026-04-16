package com.example.cartnote.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cartnote.R;
import com.example.cartnote.model.CartItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnItemInteractionListener listener;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public interface OnItemInteractionListener {
        void onStatusChanged(CartItem item);
        void onDeleteClick(CartItem item);
        void onItemClick(CartItem item);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnItemInteractionListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        
        holder.tvItemName.setText(item.getName());
        holder.tvQuantity.setText("Qty: " + item.getQuantity());
        holder.tvPrice.setText("Rp " + String.format(Locale.getDefault(), "%,d", item.getPrice()));
        holder.tvDeadline.setText("Deadline: " + item.getDeadline());
        holder.cbBought.setChecked(item.isCheckedBool());

        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            holder.ivItemImage.setImageURI(Uri.parse(item.getImageUri()));
            holder.ivItemImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivItemImage.setVisibility(View.GONE);
        }

        // Deadline logic
        updateDeadlineStatus(holder.deadlineStatusColor, item.getDeadline());

        holder.cbBought.setOnClickListener(v -> {
            item.setIsChecked(holder.cbBought.isChecked() ? 1 : 0);
            listener.onStatusChanged(item);
        });

        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
        
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    private void updateDeadlineStatus(View statusView, String deadlineStr) {
        try {
            Date deadlineDate = sdf.parse(deadlineStr);
            Date today = Calendar.getInstance().getTime();
            
            // Strip time for comparison
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(today);
            cal1.set(Calendar.HOUR_OF_DAY, 0); cal1.set(Calendar.MINUTE, 0); cal1.set(Calendar.SECOND, 0); cal1.set(Calendar.MILLISECOND, 0);
            
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(deadlineDate);
            cal2.set(Calendar.HOUR_OF_DAY, 0); cal2.set(Calendar.MINUTE, 0); cal2.set(Calendar.SECOND, 0); cal2.set(Calendar.MILLISECOND, 0);

            long diff = cal2.getTimeInMillis() - cal1.getTimeInMillis();
            long days = diff / (24 * 60 * 60 * 1000);

            if (days < 0) {
                statusView.setBackgroundColor(ContextCompat.getColor(context, R.color.deadline_over));
            } else if (days <= 2) {
                statusView.setBackgroundColor(ContextCompat.getColor(context, R.color.deadline_near));
            } else {
                statusView.setBackgroundColor(ContextCompat.getColor(context, R.color.deadline_safe));
            }
        } catch (ParseException e) {
            statusView.setBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateData(List<CartItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvQuantity, tvPrice, tvDeadline;
        CheckBox cbBought;
        ImageButton btnDelete;
        View deadlineStatusColor;
        ImageView ivItemImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            cbBought = itemView.findViewById(R.id.cbBought);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            deadlineStatusColor = itemView.findViewById(R.id.deadlineStatusColor);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
        }
    }
}
