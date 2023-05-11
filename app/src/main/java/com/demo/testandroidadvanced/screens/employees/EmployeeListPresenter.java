package com.demo.testandroidadvanced.screens.employees;

import com.demo.testandroidadvanced.api.ApiFactory;
import com.demo.testandroidadvanced.api.ApiService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EmployeeListPresenter {

    private CompositeDisposable compositeDisposable;
    private EmployeeListView view;

    public EmployeeListPresenter(EmployeeListView view) {
        this.view = view;
    }

    public void loadData() {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = apiService.getEmployees()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(employeeResponse -> view.showData(employeeResponse.getEmployees()),
                        throwable -> view.showError());
        compositeDisposable.add(disposable);
    }

    public void disposeDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}