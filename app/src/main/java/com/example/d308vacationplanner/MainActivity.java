package com.example.d308vacationplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Repository repository;
    private List<Vacation> allVacations;
    private ArrayList<String> vacationTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new Repository(getApplication());

        Button addVacationButton = findViewById(R.id.addVacationButton);
        ListView vacationListView = findViewById(R.id.vacationListView);

        addVacationButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VacationDetails.class);
            startActivity(intent);
        });

        allVacations = repository.getAllVacations();
        vacationTitles = new ArrayList<>();

        if (allVacations != null) {
            for (Vacation vacation : allVacations) {
                vacationTitles.add(vacation.getVacationTitle());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.vacation_list_item,
                vacationTitles
        );

        vacationListView.setAdapter(adapter);

        vacationListView.setOnItemClickListener((parent, view, position, id) -> {
            Vacation selectedVacation = allVacations.get(position);

            Intent intent = new Intent(MainActivity.this, VacationDetails.class);
            intent.putExtra("vacationID", selectedVacation.getVacationID());
            intent.putExtra("vacationTitle", selectedVacation.getVacationTitle());
            intent.putExtra("hotel", selectedVacation.getHotel());
            intent.putExtra("startDate", selectedVacation.getStartDate());
            intent.putExtra("endDate", selectedVacation.getEndDate());
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        allVacations = repository.getAllVacations();
        vacationTitles.clear();

        if (allVacations != null) {
            for (Vacation vacation : allVacations) {
                vacationTitles.add(vacation.getVacationTitle());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.vacation_list_item,
                vacationTitles
        );

        ListView vacationListView = findViewById(R.id.vacationListView);
        vacationListView.setAdapter(adapter);
    }
}