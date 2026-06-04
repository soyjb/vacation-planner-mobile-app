package com.example.d308vacationplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class ExcursionDetails extends AppCompatActivity {

    private EditText excursionTitleEditText;
    private EditText excursionDateEditText;

    private int excursionID;
    private int vacationID;
    private String vacationStartDate;
    private String vacationEndDate;
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);

        excursionTitleEditText = findViewById(R.id.excursionTitleEditText);
        excursionDateEditText = findViewById(R.id.excursionDateEditText);

        Button saveExcursionButton = findViewById(R.id.saveExcursionButton);
        Button deleteExcursionButton = findViewById(R.id.deleteExcursionButton);
        Button excursionAlertButton = findViewById(R.id.excursionAlertButton);
        Button backExcursionButton = findViewById(R.id.backExcursionButton);

        repository = new Repository(getApplication());

        excursionID = getIntent().getIntExtra("excursionID", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        vacationStartDate = getIntent().getStringExtra("vacationStartDate");
        vacationEndDate = getIntent().getStringExtra("vacationEndDate");

        excursionTitleEditText.setText(getIntent().getStringExtra("excursionTitle"));
        excursionDateEditText.setText(getIntent().getStringExtra("excursionDate"));

        if (excursionID == -1) {
            deleteExcursionButton.setEnabled(false);
        }

        saveExcursionButton.setOnClickListener(v -> {
            String title = excursionTitleEditText.getText().toString();
            String date = excursionDateEditText.getText().toString();

            if (title.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Enter excursion title and date", Toast.LENGTH_LONG).show();
                return;
            }

            if (!isValidDateFormat(date)) {
                Toast.makeText(this, "Date must be in MM/dd/yyyy format", Toast.LENGTH_LONG).show();
                return;
            }

            if (!isExcursionDateWithinVacation(date, vacationStartDate, vacationEndDate)) {
                Toast.makeText(this, "Excursion date must be during the vacation", Toast.LENGTH_LONG).show();
                return;
            }

            if (excursionID == -1) {
                Excursion excursion = new Excursion(0, title, date, vacationID);
                repository.insert(excursion);
            } else {
                Excursion excursion = new Excursion(excursionID, title, date, vacationID);
                repository.update(excursion);
            }

            setResult(RESULT_OK);
            finish();
        });

        deleteExcursionButton.setOnClickListener(v -> {
            if (excursionID != -1) {
                Excursion excursion = new Excursion(
                        excursionID,
                        excursionTitleEditText.getText().toString(),
                        excursionDateEditText.getText().toString(),
                        vacationID
                );

                repository.delete(excursion);
            }

            finish();
        });

        excursionAlertButton.setOnClickListener(v -> {
            String title = excursionTitleEditText.getText().toString();
            String date = excursionDateEditText.getText().toString();

            if (!isValidDateFormat(date)) {
                Toast.makeText(this, "Enter a valid excursion date first", Toast.LENGTH_LONG).show();
                return;
            }

            setExcursionAlert(date, title + " is scheduled for today!", excursionID + 3000);
        });

        backExcursionButton.setOnClickListener(v -> finish());
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
    private boolean isExcursionDateWithinVacation(String excursionDate, String startDate, String endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateFormat.setLenient(false);

        try {
            Date excursion = dateFormat.parse(excursionDate);
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            return !excursion.before(start) && !excursion.after(end);
        } catch (ParseException e) {
            return false;
        }
    }
    private void setExcursionAlert(String date, String message, int requestCode) {
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

            Toast.makeText(this, "Excursion alert set", Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date for alert", Toast.LENGTH_SHORT).show();
        }
    }
}
