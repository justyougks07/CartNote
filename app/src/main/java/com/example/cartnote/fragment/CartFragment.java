package com.example.cartnote.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cartnote.R;
import com.example.cartnote.adapter.CartAdapter;
import com.example.cartnote.model.CartItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private List<CartItem> cartItemList;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        rvCart = view.findViewById(R.id.rvCart);
        fabAdd = view.findViewById(R.id.fabAdd);

        cartItemList = new ArrayList<>();
        // Data dummy untuk testing
        cartItemList.add(new CartItem(1, "Beras 5kg", 1, 75000, 0, "2024-05-20"));
        cartItemList.add(new CartItem(2, "Minyak Goreng 2L", 2, 35000, 1, "2024-05-21"));

        adapter = new CartAdapter(getContext(), cartItemList, new CartAdapter.OnItemInteractionListener() {
            @Override
            public void onStatusChanged(CartItem item) {
                String status = item.isCheckedBool() ? "Sudah dibeli" : "Belum dibeli";
                Toast.makeText(getContext(), item.getName() + " " + status, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(CartItem item) {
                cartItemList.remove(item);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(CartItem item) {
                Toast.makeText(getContext(), "Clicked: " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCart.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            // Logika untuk tambah item (bisa memunculkan Dialog atau navigasi ke fragment lain)
            Toast.makeText(getContext(), "Fitur Tambah Item segera hadir!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
