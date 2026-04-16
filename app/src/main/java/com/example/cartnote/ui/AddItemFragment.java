package com.example.cartnote.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cartnote.R;
import com.example.cartnote.database.DatabaseHelper;
import com.example.cartnote.model.CartItem;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddItemFragment extends Fragment {

    private static final String ARG_ITEM = "item";
    private static final String ARG_CATEGORY_ID = "category_id";
    private CartItem existingItem;
    private int categoryId = -1;

    private TextInputEditText etName, etQuantity, etPrice, etDeadline;
    private android.widget.Spinner spImage;
    private Button btnSave, btnCancel;
    private TextView tvFormTitle;
    private DatabaseHelper dbHelper;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static AddItemFragment newInstance(int categoryId) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddItemFragment newInstance(CartItem item) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            existingItem = (CartItem) getArguments().getSerializable(ARG_ITEM);
            categoryId = getArguments().getInt(ARG_CATEGORY_ID, -1);
            if (existingItem != null) categoryId = existingItem.getCategoryId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        dbHelper = new DatabaseHelper(getContext());
        tvFormTitle = view.findViewById(R.id.tvFormTitle);
        etName = view.findViewById(R.id.etName);
        etQuantity = view.findViewById(R.id.etQuantity);
        etPrice = view.findViewById(R.id.etPrice);
        etDeadline = view.findViewById(R.id.etDeadline);
        spImage = view.findViewById(R.id.spImage);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        setupImageSpinner();

        if (existingItem != null) {
            tvFormTitle.setText("Edit Item");
            etName.setText(existingItem.getName());
            etQuantity.setText(String.valueOf(existingItem.getQuantity()));
            etPrice.setText(String.valueOf(existingItem.getPrice()));
            etDeadline.setText(existingItem.getDeadline());
            btnSave.setText("Update Item");
            setSelectedImage(existingItem.getImageResource());
        }

        etDeadline.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveItem());
        btnCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void showDatePicker() {
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            etDeadline.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupImageSpinner() {
        String[] imageNames = {"Tanpa Gambar", "Beras", "Minyak", "Cabe Merah"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, imageNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spImage.setAdapter(adapter);
    }

    private int getSelectedImageResource() {
        int pos = spImage.getSelectedItemPosition();
        switch (pos) {
            case 1: return R.drawable.beras;
            case 2: return R.drawable.minyak;
            case 3: return R.drawable.cabemerah;
            default: return 0;
        }
    }

    private void setSelectedImage(int resId) {
        if (resId == R.drawable.beras) spImage.setSelection(1);
        else if (resId == R.drawable.minyak) spImage.setSelection(2);
        else if (resId == R.drawable.cabemerah) spImage.setSelection(3);
        else spImage.setSelection(0);
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();
        int imageRes = getSelectedImageResource();

        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(getContext(), "Harap isi semua bidang", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(qtyStr);
        int price = Integer.parseInt(priceStr);

        if (existingItem == null) {
            CartItem item = new CartItem(0, name, quantity, price, 0, deadline, imageRes, categoryId);
            dbHelper.insertItem(item);
            Toast.makeText(getContext(), "Item berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        } else {
            existingItem.setName(name);
            existingItem.setQuantity(quantity);
            existingItem.setPrice(price);
            existingItem.setDeadline(deadline);
            existingItem.setImageResource(imageRes);
            dbHelper.updateItem(existingItem);
            Toast.makeText(getContext(), "Item berhasil diperbarui", Toast.LENGTH_SHORT).show();
        }

        getParentFragmentManager().popBackStack();
    }
}
