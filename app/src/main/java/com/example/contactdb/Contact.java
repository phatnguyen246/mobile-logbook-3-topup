package com.example.contactdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    public long contact_id;
    public String name;
    public String dob;
    public String email;
    public String imageUri;
}
