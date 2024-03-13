package school;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.util.List;

public class RMIClient extends JFrame {
    private StudentService studentService;
    private JTextField nameField, ageField, searchField;
    private JButton addButton, removeButton, editButton, searchButton;
    private JTable studentTable;

    public RMIClient() {
        super("Student Management");

        try {
            studentService = (StudentService) Naming.lookup("//localhost:1097/StudentService");
        } catch (Exception e) {
            e.printStackTrace();
        }

        nameField = new JTextField(20);
        ageField = new JTextField(10);
        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        editButton = new JButton("Edit");
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        studentTable = new JTable(new DefaultTableModel(new Object[]{"Name", "Age"}, 0));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(ageField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(editButton);

        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        controlPanel.add(inputPanel, BorderLayout.NORTH);
        controlPanel.add(searchPanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                try {
                    studentService.addStudent(name, age);
                    refreshStudentTable();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow != -1) {
                    String name = (String) studentTable.getValueAt(selectedRow, 0);
                    try {
                        studentService.removeStudent(name);
                        refreshStudentTable();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow != -1) {
                    String name = (String) studentTable.getValueAt(selectedRow, 0);
                    String newName = JOptionPane.showInputDialog(null, "Enter new name for " + name);
                    if (newName != null && !newName.isEmpty()) {
                        try {
                            studentService.removeStudent(name);
                            studentService.addStudent(newName, (int) studentTable.getValueAt(selectedRow, 1));
                            refreshStudentTable();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText();
                if (!query.isEmpty()) {
                    try {
                        List<Student> students = studentService.getAllStudents();
                        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
                        model.setRowCount(0); // Clear table
                        for (Student student : students) {
                            if (student.getName().toLowerCase().contains(query.toLowerCase())) {
                                model.addRow(new Object[]{student.getName(), student.getAge()});
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    refreshStudentTable();
                }
            }
        });

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        refreshStudentTable();
    }

    private void refreshStudentTable() {
        try {
            List<Student> students = studentService.getAllStudents();
            DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
            model.setRowCount(0); // Clear table
            for (Student student : students) {
                model.addRow(new Object[]{student.getName(), student.getAge()});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RMIClient();
    }
}

