package com.example.cartnote.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cartnote.R;
import com.example.cartnote.adapter.CategoryAdapter;
import com.example.cartnote.database.DatabaseHelper;
import com.example.cartnote.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CategoryFragment extends Fragment {

    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private DatabaseHelper dbHelper;
    private FloatingActionButton fabAddCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        dbHelper = new DatabaseHelper(getContext());
        rvCategories = view.findViewById(R.id.rvCategories);
        fabAddCategory = view.findViewById(R.id.fabAddCategory);

        setupRecyclerView();

        fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());

        return view;
    }

    private void setupRecyclerView() {
        List<Category> categories = dbHelper.getAllCategories();
        adapter = new CategoryAdapter(getContext(), categories, category -> {
            CartListFragment fragment = CartListFragment.newInstance(category.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setAdapter(adapter);
    }

    private void showAddCategoryDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_category, null);
        EditText etName = dialogView.findViewById(R.id.etCategoryName);
        EditText etDesc = dialogView.findViewById(R.id.etCategoryDesc);

        new AlertDialog.Builder(getContext())
                .setTitle("Tambah Kategori")
                .setView(dialogView)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String name = etName.getText().toString();
                    String desc = etDesc.getText().toString();
                    if (!name.isEmpty()) {
                        dbHelper.insertCategory(name, desc);
                        adapter.updateData(dbHelper.getAllCategories());
                    } else {
                        Toast.makeText(getContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
