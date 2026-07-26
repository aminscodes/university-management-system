package ui;

import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    private ArrayList<Teacher> teachers = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();

    private DefaultTableModel model;
    private Font uiFont = new Font("Tahoma", Font.PLAIN, 14);

    public MainFrame() {

        setTitle("سامانه جامع مدیریت دانشگاه");
        setSize(1000,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("سیستم مدیریت اساتید و دانشجویان", JLabel.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        title.setOpaque(true);
        title.setBackground(new Color(44, 62, 80));
        title.setForeground(Color.white);
        title.setPreferredSize(new Dimension(0, 60));
        add(title, BorderLayout.NORTH);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidePanel.setBackground(new Color(236, 240, 241));
        sidePanel.setPreferredSize(new Dimension(320, 0));

        Dimension fieldSize = new Dimension(280, 35);

        JTextField nameField = createField(fieldSize);
        JTextField ageField = createField(fieldSize);
        JTextField idField = createField(fieldSize);

        ageField.setInputVerifier(new InputVerifier() {
            public boolean verify(JComponent input) {
                return ((JTextField) input).getText().matches("\\d+");
            }
        });

        JComboBox<String> typeBox = new JComboBox<>(new String[]{"دانشجو", "استاد"});
        typeBox.setMaximumSize(fieldSize);
        typeBox.setFont(uiFont);

        sidePanel.add(new JLabel("نام و نام خانوادگی:"));
        sidePanel.add(nameField);
        sidePanel.add(new JLabel("سن:"));
        sidePanel.add(ageField);
        sidePanel.add(new JLabel("کد ملی:"));
        sidePanel.add(idField);
        sidePanel.add(new JLabel("نوع کاربری:"));
        sidePanel.add(typeBox);
        sidePanel.add(Box.createVerticalStrut(20));

        JButton addButton = createStyledButton(
                "ثبت و ورود اطلاعات تکمیلی",
                new Color(46,204,113),
                new Color(39,174,96)
        );

        addButton.setMaximumSize(new Dimension(280,45));

        sidePanel.add(addButton);
        add(sidePanel, BorderLayout.WEST);

        String[] cols = {"نام","سن","کد ملی","نقش","وضعیت"};
        model = new DefaultTableModel(cols,0);

        JTable table = new JTable(model);
        table.setFont(uiFont);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Tahoma",Font.BOLD,14));

        add(new JScrollPane(table),BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));

        JButton deleteBtn = createStyledButton("حذف",
                new Color(231,76,60),
                new Color(192,57,43)
        );
        JButton searchBtn = createStyledButton("جستجو",
                new Color(52,152,219),
                new Color(41,128,185)
        );
        JButton detailsBtn = createStyledButton("جزئیات",
                new Color(52,73,94),
                new Color(44,62,80)
        );

        bottomPanel.add(deleteBtn);
        bottomPanel.add(searchBtn);
        bottomPanel.add(detailsBtn);

        add(bottomPanel,BorderLayout.SOUTH);


        addButton.addActionListener(e -> addPerson(frameRef(), nameField, ageField, idField, typeBox));
        deleteBtn.addActionListener(e -> deletePerson(frameRef(), table));
        searchBtn.addActionListener(e -> searchPerson(frameRef()));
        detailsBtn.addActionListener(e -> showDetails(frameRef(), table));

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JFrame frameRef() { return this; }

    private boolean existsID(String id) {
        return teachers.stream().anyMatch(t -> t.getNationalId().equals(id))
                || students.stream().anyMatch(s -> s.getNationalId().equals(id));
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Teacher t : teachers)
            model.addRow(new Object[]{t.getName(), t.getAge(), t.getNationalId(), "استاد",
                    "حقوق: " + String.format("%,d", (long) t.calculateSalary())});

        for (Student s : students)
            model.addRow(new Object[]{s.getName(), s.getAge(), s.getNationalId(), "دانشجو",
                    "معدل: " + String.format("%.2f", s.calculateGPA())});
    }


    private void addPerson(JFrame frame, JTextField nameField, JTextField ageField,
                           JTextField idField, JComboBox<String> typeBox) {

        try {
            String name = nameField.getText().trim();
            String ageTxt = ageField.getText().trim();
            String id = idField.getText().trim();

            if(name.isEmpty() || id.isEmpty() || ageTxt.isEmpty()){
                JOptionPane.showMessageDialog(frame,"تمام فیلدها باید پر شوند!");
                return;
            }

            if(!id.matches("\\d{10}")){
                JOptionPane.showMessageDialog(frame,"کد ملی باید ۱۰ رقم باشد!");
                return;
            }

            int age = Integer.parseInt(ageTxt);

            if(age<=0 || age>120){
                JOptionPane.showMessageDialog(frame,"سن نامعتبر");
                return;
            }

            if (existsID(id)) {
                JOptionPane.showMessageDialog(frame,"کد ملی تکراری است!");
                return;
            }

            if(typeBox.getSelectedItem().equals("استاد")){

                int u = Integer.parseInt(JOptionPane.showInputDialog("تعداد واحد:"));
                double r = Double.parseDouble(JOptionPane.showInputDialog("حقوق هر واحد:"));

                teachers.add(new Teacher(name, age, id, u, r));

            } else {

                Student s = new Student(name, age, id);

                int count = Integer.parseInt(JOptionPane.showInputDialog("تعداد دروس:"));

                for(int i=0;i<count;i++){

                    String cName = JOptionPane.showInputDialog("نام درس:");
                    int cUnits = Integer.parseInt(JOptionPane.showInputDialog("واحد:"));
                    double cGrade = Double.parseDouble(JOptionPane.showInputDialog("نمره:"));

                    if(cUnits<=0){ i--; continue; }
                    if(cGrade<0 || cGrade>20){ i--; continue; }

                    s.addCourse(new Course(cName,cUnits,cGrade));
                }

                students.add(s);
            }

            refreshTable();
            JOptionPane.showMessageDialog(frame,"ثبت با موفقیت انجام شد");

            nameField.setText("");
            ageField.setText("");
            idField.setText("");

        } catch (Exception e){
            JOptionPane.showMessageDialog(frame,"خطا در ورودی!");
        }
    }

    private void deletePerson(JFrame frame, JTable table) {
        int r = table.getSelectedRow();
        if(r == -1){
            JOptionPane.showMessageDialog(frame,"یک ردیف انتخاب کنید");
            return;
        }

        int c = JOptionPane.showConfirmDialog(frame,"حذف شود؟","تایید",
                JOptionPane.YES_NO_OPTION);

        if(c != JOptionPane.YES_OPTION) return;

        String id = table.getValueAt(r,2).toString();
        teachers.removeIf(t -> t.getNationalId().equals(id));
        students.removeIf(s -> s.getNationalId().equals(id));

        refreshTable();
    }

    private void searchPerson(JFrame frame) {
        String id = JOptionPane.showInputDialog(frame,"کد ملی:");

        if(id == null || id.isEmpty()) return;

        for(Teacher t : teachers){
            if(t.getNationalId().equals(id)){
                JOptionPane.showMessageDialog(frame,
                        "استاد\n" +
                                "نام: "+t.getName()+
                                "\nسن: "+t.getAge()+
                                "\nحقوق: "+t.calculateSalary());
                return;
            }
        }

        for(Student s : students){
            if(s.getNationalId().equals(id)){
                JOptionPane.showMessageDialog(frame,
                        "دانشجو\n" +
                                "نام: "+s.getName()+
                                "\nسن: "+s.getAge()+
                                "\nمعدل: "+String.format("%.2f", s.calculateGPA()));
                return;
            }
        }

        JOptionPane.showMessageDialog(frame,"پیدا نشد");
    }

    private void showDetails(JFrame frame, JTable table) {
        int r = table.getSelectedRow();
        if(r == -1){
            JOptionPane.showMessageDialog(frame,"انتخاب کنید");
            return;
        }

        String id = table.getValueAt(r,2).toString();

        for(Student s : students){
            if(s.getNationalId().equals(id)){

                StringBuilder x = new StringBuilder();

                x.append("نام: ").append(s.getName()).append("\n");
                x.append("سن: ").append(s.getAge()).append("\n");
                x.append("معدل: ").append(String.format("%.2f", s.calculateGPA())).append("\n\n");

                x.append("لیست دروس:\n");

                for(Course c : s.courses){
                    x.append(c.name).append(" | ")
                            .append("واحد: ").append(c.units).append(" | ")
                            .append("نمره: ").append(c.grade).append("\n");
                }

                JOptionPane.showMessageDialog(frame, x.toString());
                return;
            }
        }
    }


    private JTextField createField(Dimension d){
        JTextField f = new JTextField();
        f.setFont(uiFont);
        f.setMaximumSize(d);
        return f;
    }

    private JButton createStyledButton(String text,Color normal,Color hover){

        JButton btn = new JButton(text);

        btn.setFont(new Font("Tahoma",Font.BOLD,14));
        btn.setForeground(Color.WHITE);

        btn.setFocusPainted(false);
        btn.setBackground(normal);
        btn.setBorder(new RoundedBorder(12));
        btn.setOpaque(true);

        btn.setPreferredSize(new Dimension(140,40));

        btn.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent evt){
                btn.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt){
                btn.setBackground(normal);
            }
        });

        return btn;
    }

}
