package com.demo.testandroidadvanced.screens.employees;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.demo.testandroidadvanced.api.ApiFactory;
import com.demo.testandroidadvanced.api.ApiService;
import com.demo.testandroidadvanced.data.AppDatabase;
import com.demo.testandroidadvanced.pojo.Employee;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EmployeeViewModel extends AndroidViewModel {
    private AppDatabase db;
    private LiveData<List<Employee>> employees;

    private CompositeDisposable compositeDisposable;

    public EmployeeViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        employees = db.employeeDao().getAllEmployees();
    }

    public LiveData<List<Employee>> getEmployees() {
        return employees;
    }

    private void insertEmployees(List<Employee> employees) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if (employees != null) {
                db.employeeDao().insertEmployees(employees);
            }
        });
    }

    private void deleteAllEmployees() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            db.employeeDao().deleteAllEmployees();
        });
    }

    public void loadData() {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = apiService.getEmployees()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(employeeResponse -> {
                            deleteAllEmployees();
                            insertEmployees(employeeResponse.getEmployees());
                        },
                        throwable -> {
                        });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}