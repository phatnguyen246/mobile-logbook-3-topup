package com.example.contactdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class}, version = 2)
public abstract class AppDb extends RoomDatabase {
    public abstract ContactDao contactDao();


}
