package com.demo.testandroidadvanced.screens.employees;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.demo.testandroidadvanced.api.ApiFactory;
import com.demo.testandroidadvanced.api.ApiService;
import com.demo.testandroidadvanced.data.AppDatabase;
import com.demo.testandroidadvanced.pojo.Employee;
import com.demo.testandroidadvanced.pojo.EmployeeResponse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class EmployeeViewModel extends AndroidViewModel {
    private AppDatabase db;
    private LiveData<List<Employee>> employees;
    private MutableLiveData<Throwable> errors;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private CompositeDisposable compositeDisposable;

    public EmployeeViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        employees = db.employeeDao().getAllEmployees();
        errors = new MutableLiveData<>();
    }

    public LiveData<List<Employee>> getEmployees() {
        return employees;
    }

    // геттеру оставляем тип LiveData, чтобы не было возможности его изменить из активности
    public LiveData<Throwable> getErrors() {
        return errors;
    }

    public void clearErrors() {
        errors.setValue(null);
    }

    private void insertEmployees(List<Employee> list) {
        synchronized (executor) {
            executor.execute(() -> {
                if (list != null) {
                    db.employeeDao().insertEmployees(list);
                }
            });
        }
    }

    private void deleteAllEmployees() {
        synchronized (executor) {
            executor.execute(() -> {
                db.employeeDao().deleteAllEmployees();
            });
        }
    }

    public void loadData() {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = apiService.getEmployees()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<EmployeeResponse>() {
                               @Override
                               public void accept(EmployeeResponse employeeResponse) throws Exception {
                                   deleteAllEmployees();
                                   insertEmployees(employeeResponse.getEmployees());
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                errors.setValue(throwable);
                            }
                        });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}