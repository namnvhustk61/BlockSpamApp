package com.stork.blockspam.database.model.DbBlockPhone;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DbBlockPhoneDAO {
    @Insert
    void insert(DbBlockPhone... items);

    @Query("SELECT * FROM TbDbBlockPhone")
    List<DbBlockPhone> getAll();

    @Query("DELETE FROM TbDbBlockPhone")
    void deleteAll();
}
