package com.example.cartnote.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private ImageView ivSelectedImage;
    private Button btnPickImage, btnRemoveImage, btnSave, btnCancel;
    private TextView tvFormTitle;
    private DatabaseHelper dbHelper;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Take persistable URI permission if needed (optional but recommended for long term access)
                    if (selectedImageUri != null) {
                        getContext().getContentResolver().takePersistableUriPermission(selectedImageUri, 
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        ivSelectedImage.setImageURI(selectedImageUri);
                        btnRemoveImage.setVisibility(View.VISIBLE);
                    }
                }
            }
    );

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
        
        ivSelectedImage = view.findViewById(R.id.ivSelectedImage);
        btnPickImage = view.findViewById(R.id.btnPickImage);
        btnRemoveImage = view.findViewById(R.id.btnRemoveImage);
        
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        if (existingItem != null) {
            tvFormTitle.setText("Edit Item");
            etName.setText(existingItem.getName());
            etQuantity.setText(String.valueOf(existingItem.getQuantity()));
            etPrice.setText(String.valueOf(existingItem.getPrice()));
            etDeadline.setText(existingItem.getDeadline());
            btnSave.setText("Update Item");
            
            if (existingItem.getImageUri() != null) {
                selectedImageUri = Uri.parse(existingItem.getImageUri());
                ivSelectedImage.setImageURI(selectedImageUri);
                btnRemoveImage.setVisibility(View.VISIBLE);
            }
        }

        etDeadline.setOnClickListener(v -> showDatePicker());
        btnPickImage.setOnClickListener(v -> pickImage());
        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUri = null;
            ivSelectedImage.setImageResource(android.R.drawable.ic_menu_gallery);
            btnRemoveImage.setVisibility(View.GONE);
        });
        
        btnSave.setOnClickListener(v -> saveItem());
        btnCancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void showDatePicker() {
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            etDeadline.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();
        String imageUri = selectedImageUri != null ? selectedImageUri.toString() : null;

        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(getContext(), "Harap isi semua bidang", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(qtyStr);
        int price = Integer.parseInt(priceStr);

        if (existingItem == null) {
            CartItem item = new CartItem(0, name, quantity, price, 0, deadline, imageUri, categoryId);
            dbHelper.insertItem(item);
            Toast.makeText(getContext(), "Item berhasil ditambahkan", Toast.LENGTH_SHORT).show();
        } else {
            existingItem.setName(name);
            existingItem.setQuantity(quantity);
            existingItem.setPrice(price);
            existingItem.setDeadline(deadline);
            existingItem.setImageUri(imageUri);
            dbHelper.updateItem(existingItem);
            Toast.makeText(getContext(), "Item berhasil diperbarui", Toast.LENGTH_SHORT).show();
        }

        getParentFragmentManager().popBackStack();
    }
}
