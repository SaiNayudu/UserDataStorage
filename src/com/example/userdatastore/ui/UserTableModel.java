package com.example.userdatastore.ui;

import com.example.userdatastore.model.User;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class UserTableModel extends AbstractTableModel {
    private final String[] cols = { "ID", "Name", "Email", "Phone", "Department", "DOB", "Gender" };
    private final List<User> rows = new ArrayList<>();

    public void setRows(List<User> users) {
        rows.clear();
        if (users != null) rows.addAll(users);
        fireTableDataChanged();
    }

    public User getAt(int r) {
        if (r < 0 || r >= rows.size()) return null;
        return rows.get(r);
    }

    @Override
    public int getRowCount() { return rows.size(); }

    @Override
    public int getColumnCount() { return cols.length; }

    @Override
    public String getColumnName(int column) { return cols[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        User u = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> u.getId();
            case 1 -> u.getName();
            case 2 -> u.getEmail();
            case 3 -> u.getPhone();
            case 4 -> u.getDepartment();
            case 5 -> u.getDob();
            case 6 -> u.getGender();
            default -> null;
        };
    }
}
