package com.example.userdatastore.ui;

import com.example.userdatastore.model.User;
import com.example.userdatastore.storage.UserStore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;

public class MainFrame extends JFrame {
    private final UserStore store = new UserStore();
    private final UserTableModel tableModel = new UserTableModel();
    private final JTable table = new JTable(tableModel);

    private final JTextField idField = new JTextField(12);
    private final JTextField nameField = new JTextField(12);
    private final JTextField emailField = new JTextField(12);
    private final JTextField phoneField = new JTextField(12);
    private final JTextField deptField = new JTextField(12);
    private final JTextField dobField = new JTextField(10);
    private final JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});

    private final File defaultFile = new File("users.txt");

    public MainFrame() {
        setTitle("User Data Storage");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new BorderLayout(8,8));
        p.setBorder(new EmptyBorder(8,8,8,8));

        p.add(buildFormPanel(), BorderLayout.NORTH);
        p.add(buildTablePanel(), BorderLayout.CENTER);
        p.add(buildControlPanel(), BorderLayout.SOUTH);

        setContentPane(p);

        loadOnStart();
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("ID:")); form.add(idField);
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Email:")); form.add(emailField);
        form.add(new JLabel("Phone:")); form.add(phoneField);
        form.add(new JLabel("Dept:")); form.add(deptField);
        form.add(new JLabel("DOB(YYYY-MM-DD):")); form.add(dobField);
        form.add(new JLabel("Gender:")); form.add(genderCombo);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton delBtn = new JButton("Delete");

        addBtn.addActionListener(this::onAdd);
        updateBtn.addActionListener(this::onUpdate);
        delBtn.addActionListener(this::onDelete);

        form.add(addBtn); form.add(updateBtn); form.add(delBtn);

        return form;
    }

    private JScrollPane buildTablePanel() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::onTableSelection);
        return new JScrollPane(table);
    }

    private JPanel buildControlPanel() {
        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton saveBtn = new JButton("Save");
        JButton loadBtn = new JButton("Load");
        JButton exportBtn = new JButton("Export CSV");
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search by ID");

        saveBtn.addActionListener(e -> {
            try {
                store.saveToFile(defaultFile);
                store.loadFromFile(defaultFile); // reload
                tableModel.setRows(store.getAll());
                JOptionPane.showMessageDialog(this, "Users saved successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
            }
        });

        loadBtn.addActionListener(e -> {
            try {
                store.loadFromFile(defaultFile);
                tableModel.setRows(store.getAll());
                JOptionPane.showMessageDialog(this, "Users loaded successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading: " + ex.getMessage());
            }
        });

        exportBtn.addActionListener(e -> {
            File csvFile = new File("users.csv");
            try (PrintWriter pw = new PrintWriter(csvFile)) {
                pw.println("ID,Name,Email,Phone,Department,DOB,Gender");
                for (User u : store.getAll()) {
                    pw.printf("%s,%s,%s,%s,%s,%s,%s%n",
                            u.getId(), u.getName(), u.getEmail(), u.getPhone(),
                            u.getDepartment(), u.getDob(), u.getGender());
                }
                JOptionPane.showMessageDialog(this, "Exported to " + csvFile.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting: " + ex.getMessage());
            }
        });

        searchBtn.addActionListener(e -> {
            String id = searchField.getText().trim();
            if (id.isEmpty()) return;
            User u = store.get(id);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "User not found!");
            } else {
                JOptionPane.showMessageDialog(this, u.toString());
            }
        });

        ctrl.add(saveBtn);
        ctrl.add(loadBtn);
        ctrl.add(exportBtn);
        ctrl.add(searchField);
        ctrl.add(searchBtn);
        return ctrl;
    }

    private void onAdd(ActionEvent e) {
        User u = new User(
                idField.getText(), nameField.getText(), emailField.getText(),
                phoneField.getText(), deptField.getText(), dobField.getText(),
                (String) genderCombo.getSelectedItem()
        );
        store.add(u);
        tableModel.setRows(store.getAll());
    }

    private void onUpdate(ActionEvent e) {
        User u = new User(
                idField.getText(), nameField.getText(), emailField.getText(),
                phoneField.getText(), deptField.getText(), dobField.getText(),
                (String) genderCombo.getSelectedItem()
        );
        store.update(u);
        tableModel.setRows(store.getAll());
    }

    private void onDelete(ActionEvent e) {
        String id = idField.getText();
        store.delete(id);
        tableModel.setRows(store.getAll());
    }

    private void onTableSelection(ListSelectionEvent e) {
        int row = table.getSelectedRow();
        User u = tableModel.getAt(row);
        if (u != null) {
            idField.setText(u.getId());
            nameField.setText(u.getName());
            emailField.setText(u.getEmail());
            phoneField.setText(u.getPhone());
            deptField.setText(u.getDepartment());
            dobField.setText(u.getDob());
            genderCombo.setSelectedItem(u.getGender());
        }
    }

    private void loadOnStart() {
        try {
            store.loadFromFile(defaultFile);
            tableModel.setRows(store.getAll());
        } catch (Exception ignored) {}
    }
}
