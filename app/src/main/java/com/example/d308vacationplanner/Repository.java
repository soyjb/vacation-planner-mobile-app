package com.example.d308vacationplanner;

import android.app.Application;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private VacationDAO vacationDAO;
    private ExcursionDAO excursionDAO;
    private List<Vacation> allVacations;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(application);
        vacationDAO = db.vacationDAO();
        excursionDAO = db.excursionDAO();
    }

    public List<Vacation> getAllVacations() {
        databaseExecutor.execute(() -> {
            allVacations = vacationDAO.getAllVacations();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return allVacations;
    }

    public void insert(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDAO.insert(vacation));
    }

    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDAO.update(vacation));
    }

    public void delete(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDAO.delete(vacation));
    }

    public List<Excursion> getAssociatedExcursions(int vacationID) {
        final List<Excursion>[] associatedExcursions = new List[]{null};

        databaseExecutor.execute(() -> {
            associatedExcursions[0] = excursionDAO.getAssociatedExcursions(vacationID);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return associatedExcursions[0];
    }

    public int getAssociatedExcursionCount(int vacationID) {
        final int[] count = new int[1];

        databaseExecutor.execute(() -> {
            count[0] = excursionDAO.getAssociatedExcursionCount(vacationID);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return count[0];
    }

    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDAO.insert(excursion));
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDAO.update(excursion));
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDAO.delete(excursion));
    }
}
