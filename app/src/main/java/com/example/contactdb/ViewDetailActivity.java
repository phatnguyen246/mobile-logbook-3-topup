package com.example.contactdb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

public class ViewDetailActivity extends AppCompatActivity {

    private AppDb appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        appDatabase = Room
                .databaseBuilder(getApplicationContext(), AppDb.class, "sqlite_contact_db")
                .allowMainThreadQueries() // For simplicity, don't use this in production
                .fallbackToDestructiveMigration()
                .build();

        Intent intent = getIntent();
        long contactId = intent.getLongExtra("CONTACT_ID", -1L);


        if (contactId != -1L) {
            Contact contact = appDatabase.contactDao().getContactById(contactId);

            TextView txtName = findViewById(R.id.name);
            TextView txtDob = findViewById(R.id.dob);
            TextView txtEmail = findViewById(R.id.email);
            ImageView imgAvatar = findViewById(R.id.avatar);

            txtName.setText(contact.name);
            txtDob.setText(contact.dob);
            txtEmail.setText(contact.email);
            if (contact.imageUri != null && !contact.imageUri.isEmpty()) {
                imgAvatar.setImageURI(Uri.parse(contact.imageUri));
            } else {
                imgAvatar.setImageResource(R.drawable.ic_default_avatar);
            }

        } else {
            Toast.makeText(this, "Error: contact_id not received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }
}