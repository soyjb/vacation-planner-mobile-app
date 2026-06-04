package com.example.d308vacationplanner;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExcursionDAO {

    @Insert
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT * FROM excursions WHERE vacationID = :vacationID ORDER BY excursionID ASC")
    List<Excursion> getAssociatedExcursions(int vacationID);

    @Query("SELECT COUNT(*) FROM excursions WHERE vacationID = :vacationID")
    int getAssociatedExcursionCount(int vacationID);
}