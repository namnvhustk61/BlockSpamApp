package com.stork.blockspam.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TbCallPhone")
public class CallPhone{
    @PrimaryKey
    public Long id;
    public String name;
    public String phone;
}
