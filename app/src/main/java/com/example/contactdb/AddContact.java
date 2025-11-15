package com.example.contactdb;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AddContact extends AppCompatActivity {

    private ImageView imgPreview;
    private Uri pickedImageTempUri;

    private AppDb appDatabase;

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    pickedImageTempUri = uri;
                    imgPreview.setImageURI(uri); // hiển thị ngay
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_contact);
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

        imgPreview = findViewById(R.id.previewImg);
        Button selectBtn = findViewById(R.id.selectImg);
        Button saveDetailsButton = findViewById(R.id.btn_submit);

        selectBtn.setOnClickListener(v -> pickImage.launch("image/*"));
        saveDetailsButton.setOnClickListener(v -> saveDetails());


    }

    private void saveDetails() {

        // Get references to the EditText views and read their content
        EditText nameTxt = findViewById(R.id.inputName);
        EditText dobTxt = findViewById(R.id.inputDoB);
        EditText emailTxt = findViewById(R.id.inputEmail);

        String name = nameTxt.getText().toString();
        String dob = dobTxt.getText().toString();
        String email = emailTxt.getText().toString();

        Contact contact = new Contact();
        contact.name = name;
        contact.dob = dob;
        contact.email = email;
        contact.imageUri = null;

        // Calls the insertDetails method we created
        long contactId = appDatabase.contactDao().insertContact(contact);

        if (pickedImageTempUri != null) {
            File dir = new File(getFilesDir(), "images/contacts");
            if (!dir.exists()) dir.mkdirs();

            String ext = guessExt(pickedImageTempUri);
            File dst = new File(dir, contactId + ext);
            if (copyUriToFile(pickedImageTempUri, dst)) {
                appDatabase.contactDao().updateImageUri(contactId, Uri.fromFile(dst).toString());
            }
        }

        // Shows a toast with the automatically generated id
        Toast.makeText(this, "Person has been created with id: " + contactId,
                Toast.LENGTH_LONG
        ).show();

        finish();
    }

    private boolean copyUriToFile(Uri src, File dst) {
        try (InputStream in = getContentResolver().openInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192]; int n;
            while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private String guessExt(Uri uri) {
        ContentResolver cr = getContentResolver();
        String mime = cr.getType(uri);
        if ("image/png".equals(mime)) return ".png";
        if ("image/webp".equals(mime)) return ".webp";
        return ".jpg";
    }


}