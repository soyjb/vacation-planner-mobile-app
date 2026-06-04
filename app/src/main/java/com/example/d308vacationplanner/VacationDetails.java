package com.example.d308vacationplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class VacationDetails extends AppCompatActivity {

    private EditText vacationTitleEditText;
    private EditText hotelEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;

    private int vacationID;
    private Repository repository;

    private List<Excursion> associatedExcursions;
    private ArrayList<String> excursionTitles;
    private ListView excursionListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        vacationTitleEditText = findViewById(R.id.vacationTitleEditText);
        hotelEditText = findViewById(R.id.hotelEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);

        Button saveVacationButton = findViewById(R.id.saveVacationButton);
        Button deleteVacationButton = findViewById(R.id.deleteVacationButton);
        Button startAlertButton = findViewById(R.id.startAlertButton);
        Button endAlertButton = findViewById(R.id.endAlertButton);
        Button shareVacationButton = findViewById(R.id.shareVacationButton);
        Button addExcursionButton = findViewById(R.id.addExcursionButton);
        Button backVacationButton = findViewById(R.id.backVacationButton);

        repository = new Repository(getApplication());
        excursionListView = findViewById(R.id.excursionListView);

        startAlertButton.setOnClickListener(v -> {
            String title = vacationTitleEditText.getText().toString();
            String startDate = startDateEditText.getText().toString();

            if (!isValidDateFormat(startDate)) {
                Toast.makeText(this, "Enter a valid start date first", Toast.LENGTH_LONG).show();
                return;
            }

            setVacationAlert(startDate, title + " is starting today!", vacationID + 1000);
        });

        endAlertButton.setOnClickListener(v -> {
            String title = vacationTitleEditText.getText().toString();
            String endDate = endDateEditText.getText().toString();

            if (!isValidDateFormat(endDate)) {
                Toast.makeText(this, "Enter a valid end date first", Toast.LENGTH_LONG).show();
                return;
            }

            setVacationAlert(endDate, title + " is ending today!", vacationID + 2000);
        });

        shareVacationButton.setOnClickListener(v -> {
            String title = vacationTitleEditText.getText().toString();
            String hotel = hotelEditText.getText().toString();
            String startDate = startDateEditText.getText().toString();
            String endDate = endDateEditText.getText().toString();

            String vacationDetails =
                    "Vacation Title: " + title + "\n" +
                            "Hotel / Accommodation: " + hotel + "\n" +
                            "Start Date: " + startDate + "\n" +
                            "End Date: " + endDate;

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, vacationDetails);

            Intent shareIntent = Intent.createChooser(sendIntent, "Share Vacation Details");
            startActivity(shareIntent);
        });

        addExcursionButton.setOnClickListener(v -> {

            if (vacationID == -1) {
                Toast.makeText(this, "Save the vacation before adding excursions", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
            intent.putExtra("vacationID", vacationID);
            intent.putExtra("vacationStartDate", startDateEditText.getText().toString());
            intent.putExtra("vacationEndDate", endDateEditText.getText().toString());
            startActivityForResult(intent, 1);
        });

        backVacationButton.setOnClickListener(v -> finish());

        vacationID = getIntent().getIntExtra("vacationID", -1);

        if(vacationID == -1) {
            deleteVacationButton.setEnabled(false);
        }

        vacationTitleEditText.setText(getIntent().getStringExtra("vacationTitle"));
        hotelEditText.setText(getIntent().getStringExtra("hotel"));
        startDateEditText.setText(getIntent().getStringExtra("startDate"));
        endDateEditText.setText(getIntent().getStringExtra("endDate"));

        saveVacationButton.setOnClickListener(v -> {
            String title = vacationTitleEditText.getText().toString();
            String hotel = hotelEditText.getText().toString();
            String startDate = startDateEditText.getText().toString();
            String endDate = endDateEditText.getText().toString();

            if (!isValidDateFormat(startDate) || !isValidDateFormat(endDate)) {
                Toast.makeText(this, "Dates must be in MM/dd/yyyy format", Toast.LENGTH_LONG).show();
                return;
            }

            if (!isEndDateAfterStartDate(startDate, endDate)) {
                Toast.makeText(this, "End date must be after start date", Toast.LENGTH_LONG).show();
                return;
            }

            if (vacationID == -1) {
                Vacation vacation = new Vacation(0, title, hotel, startDate, endDate);
                repository.insert(vacation);
            } else {
                Vacation vacation = new Vacation(vacationID, title, hotel, startDate, endDate);
                repository.update(vacation);
            }

            finish();
        });

        deleteVacationButton.setOnClickListener(v -> {
            if (vacationID != -1) {

                int excursionCount = repository.getAssociatedExcursionCount(vacationID);

                if (excursionCount > 0) {
                    Toast.makeText(this, "Cannot delete vacation with associated excursions", Toast.LENGTH_LONG).show();
                    return;
                }

                Vacation vacation = new Vacation(
                        vacationID,
                        vacationTitleEditText.getText().toString(),
                        hotelEditText.getText().toString(),
                        startDateEditText.getText().toString(),
                        endDateEditText.getText().toString()
                );

                repository.delete(vacation);
            }

            finish();
        });
        if (vacationID != -1) {
            loadAssociatedExcursions();
        }
    }
    private boolean isValidDateFormat(String date) {

        if (date == null || date.length() != 10) {
            return false;
        }

        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        dateFormat.setLenient(false);

        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    private boolean isEndDateAfterStartDate(String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateFormat.setLenient(false);

        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            return end.after(start);
        } catch (ParseException e) {
            return false;
        }
    }
    private void setVacationAlert(String date, String message, int requestCode) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        try {
            Date alertDate = dateFormat.parse(date);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(alertDate);

            Intent intent = new Intent(this, MyReceiver.class);
            intent.putExtra("message", message);

            PendingIntent sender = PendingIntent.getBroadcast(
                    this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

            Toast.makeText(this, "Alert set", Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date for alert", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadAssociatedExcursions() {
        associatedExcursions = repository.getAssociatedExcursions(vacationID);
        excursionTitles = new ArrayList<>();

        if (associatedExcursions != null) {
            for (Excursion excursion : associatedExcursions) {
                excursionTitles.add(excursion.getExcursionTitle() + " - " + excursion.getExcursionDate());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.excursion_list_item,
                excursionTitles
        );

        excursionListView.setAdapter(adapter);

        excursionListView.setOnItemClickListener((parent, view, position, id) -> {

            Excursion selectedExcursion = associatedExcursions.get(position);

            Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);

            intent.putExtra("excursionID", selectedExcursion.getExcursionID());
            intent.putExtra("excursionTitle", selectedExcursion.getExcursionTitle());
            intent.putExtra("excursionDate", selectedExcursion.getExcursionDate());
            intent.putExtra("vacationID", vacationID);
            intent.putExtra("vacationStartDate", startDateEditText.getText().toString());
            intent.putExtra("vacationEndDate", endDateEditText.getText().toString());

            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (vacationID != -1) {
            loadAssociatedExcursions();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadAssociatedExcursions();
        }
    }
}