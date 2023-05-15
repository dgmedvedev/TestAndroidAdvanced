package com.demo.testandroidadvanced.screens.employees;

import com.demo.testandroidadvanced.pojo.Employee;

import java.util.List;

public interface EmployeeListView {
    void showData(List<Employee> employees);
    void showError();
}