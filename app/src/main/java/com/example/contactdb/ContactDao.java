package com.example.contactdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert
    long insertContact(Contact contact);

    @Query("SELECT * FROM contacts WHERE contact_id = :contactId LIMIT 1")
    Contact getContactById(long contactId);

    @Update
    void updateContact(Contact contact);

    @Query("UPDATE contacts SET imageUri = :uri WHERE contact_id = :contactId")
    int updateImageUri(long contactId, String uri);

    @Query("SELECT * FROM contacts ORDER BY name")
    List<Contact> getAllContacts();
    @Query("SELECT * FROM contacts ORDER BY name")
    LiveData<List<Contact>> observeAllContacts();

    @Delete
    void deleteContact(Contact contact);

}
