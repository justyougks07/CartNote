package com.example.cartnote.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cartnote.R;
import com.example.cartnote.adapter.CartAdapter;
import com.example.cartnote.database.DatabaseHelper;
import com.example.cartnote.model.CartItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

public class CartListFragment extends Fragment implements CartAdapter.OnItemInteractionListener {

    private static final String ARG_CATEGORY_ID = "category_id";
    private int categoryId = -1;

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView tvTotalItems, tvTotalPrice;
    private FloatingActionButton fabAdd;

    public static CartListFragment newInstance(int categoryId) {
        CartListFragment fragment = new CartListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_list, container, false);

        dbHelper = new DatabaseHelper(getContext());
        rvCart = view.findViewById(R.id.rvCart);
        tvTotalItems = view.findViewById(R.id.tvTotalItems);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        fabAdd = view.findViewById(R.id.fabAdd);

        setupRecyclerView();
        updateSummary();

        fabAdd.setOnClickListener(v -> {
            AddItemFragment fragment = AddItemFragment.newInstance(categoryId);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void setupRecyclerView() {
        List<CartItem> items = categoryId == -1 ? dbHelper.getAllItems() : dbHelper.getItemsByCategory(categoryId);
        adapter = new CartAdapter(getContext(), items, this);
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCart.setAdapter(adapter);
    }

    private void updateSummary() {
        List<CartItem> items = categoryId == -1 ? dbHelper.getAllItems() : dbHelper.getItemsByCategory(categoryId);
        int totalQty = 0;
        long totalPrice = 0;
        int boughtCount = 0;

        for (CartItem item : items) {
            totalQty += item.getQuantity();
            totalPrice += (long) item.getPrice() * item.getQuantity();
            if (item.getIsChecked() == 1) boughtCount++;
        }

        tvTotalItems.setText(String.format(Locale.getDefault(), "Items: %d (%d dibeli)", items.size(), boughtCount));
        tvTotalPrice.setText(String.format(Locale.getDefault(), "Total: Rp %,d", totalPrice));
    }

    @Override
    public void onStatusChanged(CartItem item) {
        dbHelper.updateItem(item);
        updateSummary();
    }

    @Override
    public void onDeleteClick(CartItem item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Item")
                .setMessage("Apakah Anda yakin ingin menghapus " + item.getName() + "?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    dbHelper.deleteItem(item.getId());
                    adapter.updateData(categoryId == -1 ? dbHelper.getAllItems() : dbHelper.getItemsByCategory(categoryId));
                    updateSummary();
                    Toast.makeText(getContext(), "Item dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public void onItemClick(CartItem item) {
        AddItemFragment fragment = AddItemFragment.newInstance(item);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateData(categoryId == -1 ? dbHelper.getAllItems() : dbHelper.getItemsByCategory(categoryId));
        updateSummary();
    }
}
