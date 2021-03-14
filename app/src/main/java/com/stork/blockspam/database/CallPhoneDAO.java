package com.stork.blockspam.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CallPhoneDAO {
    @Insert
    void insert(CallPhone... items);
    @Update
    void update(CallPhone... items);
    @Delete
    void delete(CallPhone item);

    @Query("SELECT * FROM TbCallPhone ORDER BY id DESC")
    List<CallPhone> getAll();

    @Query("SELECT * FROM TbCallPhone WHERE id = :id")
    CallPhone getById(Long id);

    @Query("SELECT * FROM TbCallPhone WHERE phone = :phone")
    List<CallPhone> getByPhone(String phone);

    @Query("UPDATE TbCallPhone SET status = :statusNew")
    void updateStatusAll(String statusNew);

    @Query("DELETE FROM TbCallPhone")
    void deleteAll();
}
