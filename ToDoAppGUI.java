import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

public class ToDoAppGUI extends JFrame {
    private DatabaseHelper db;
    private JList<Task> taskList;
    private DefaultListModel<Task> listModel;
    private JTextArea notifLabel;

    private final Font fontJudul = new Font("SansSerif", Font.BOLD, 18);
    private final Font fontDetail = new Font("SansSerif", Font.PLAIN, 13);
    private final Font fontCountdown = new Font("SansSerif", Font.BOLD, 14);
    
    private final Color colorBg = new Color(245, 245, 245);
    private final Color colorPanel = Color.WHITE;
    private final Color colorNotifBg = new Color(255, 253, 235);
    private final Color colorHeader = new Color(220, 220, 220);

    public ToDoAppGUI() {
        db = new DatabaseHelper();
        setTitle("🌸Rainy's To-Do List🌸");
        setSize(800, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout(0, 10));
        getContentPane().setBackground(colorBg);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        taskList.setCellRenderer(new TaskListCellRenderer());
        taskList.setBackground(colorBg); 

        JScrollPane scroll = new JScrollPane(taskList);
        scroll.setBorder(BorderFactory.createLineBorder(colorHeader));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 

        JButton btnAdd = new JButton("Tambah To-Do List");
        JButton btnDone = new JButton("Yeay! Selesai gusyy😁");
        btnAdd.setFont(fontDetail);
        btnDone.setFont(fontDetail);
        notifLabel = new JTextArea("🔔 Menunggu notifikasi...", 3, 40);
        notifLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        notifLabel.setEditable(false);
        notifLabel.setLineWrap(true);
        notifLabel.setWrapStyleWord(true);
        notifLabel.setBackground(colorNotifBg);
        notifLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 220, 180)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JScrollPane notifScroll = new JScrollPane(notifLabel);
        notifScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        notifScroll.setBorder(null);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(colorPanel);
        panel.add(btnAdd);
        panel.add(btnDone);
        add(notifScroll, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER); 
        add(panel, BorderLayout.SOUTH);
        btnAdd.addActionListener(e -> tambahTugas());
        btnDone.addActionListener(e -> tandaiSelesai());

        new NotificationThread(db, this).start();
    }
    class TaskListCellRenderer extends JPanel implements ListCellRenderer<Task> {
        
        private JLabel lblJudul = new JLabel();
        private JLabel lblDeadline = new JLabel();
        private JLabel lblSisaWaktu = new JLabel();
        private JLabel lblStatus = new JLabel();
        
        private Border defaultBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, colorHeader), 
            BorderFactory.createEmptyBorder(10, 15, 10, 15) 
        );
        private Border selectedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 1, 0, Color.BLUE), 
            BorderFactory.createEmptyBorder(10, 10, 10, 15) 
        );
        private final Color colorDoneText = new Color(150, 150, 150);
        private final Color colorOverdueBg = new Color(255, 230, 230);
        private final Color colorOverdueText = new Color(180, 0, 0);
        private final Color colorSelected = new Color(230, 240, 255); 

        public TaskListCellRenderer() {
            setLayout(new BorderLayout(15, 0)); 
            
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false); 

            lblJudul.setFont(fontJudul);
            lblDeadline.setFont(fontDetail);
            lblSisaWaktu.setFont(fontCountdown);
            lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 24)); 
            
            textPanel.add(lblJudul);
            textPanel.add(Box.createVerticalStrut(5)); 
            textPanel.add(lblDeadline);
            textPanel.add(lblSisaWaktu);

            add(textPanel, BorderLayout.CENTER);
            add(lblStatus, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Task> list, Task task,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            
            String status = task.isDone() ? "✅" : "⏳";
            String sisaWaktu = task.getCountdown();
            lblJudul.setText(task.getTitle());
            lblDeadline.setText("Deadline: " + task.getDeadline().toString().replace(".0", ""));
            lblSisaWaktu.setText("Sisa Waktu: " + sisaWaktu);
            lblStatus.setText(status);

            lblJudul.setForeground(Color.BLACK);
            lblDeadline.setForeground(Color.DARK_GRAY);
            lblSisaWaktu.setForeground(Color.BLACK);
            
            if (task.isDone()) { // Selesai
                setBackground(new Color(248, 248, 248)); 
                lblJudul.setForeground(colorDoneText);
                lblDeadline.setForeground(colorDoneText);
                lblSisaWaktu.setForeground(colorDoneText);
            } else if (sisaWaktu.startsWith("Waktu Habis!")) { 
                setBackground(colorOverdueBg);
                lblJudul.setForeground(colorOverdueText);
                lblSisaWaktu.setForeground(colorOverdueText);
            } else { 
                setBackground(Color.WHITE);
            }

            if (isSelected) {
                setBackground(colorSelected);
                setBorder(selectedBorder);
            } else {
                setBorder(defaultBorder);
            }

            return this; 
        }
    }

    private void tambahTugas() {
        String title = JOptionPane.showInputDialog(this, "Deadline apa hari ini?:");
        if (title == null || title.isBlank()) return;

        String date = JOptionPane.showInputDialog(this, "Deadline Tanggal (YYYY-MM-DD):");
        if (date == null || !date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah😒😒! Harusnya YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String time = JOptionPane.showInputDialog(this, "Deadline Waktu (HH:MM):", "00:00");
        if (time == null || !time.matches("^([01]\\d|2[0-3]):([0-5]\\d)$")) {
            JOptionPane.showMessageDialog(this, "Format waktu salah! Harusnya HH:MM (24 jam).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fullDeadline = date + " " + time + ":00"; 
        db.addTask(title, fullDeadline);
    }

    private void tandaiSelesai() {
        Task selectedTask = taskList.getSelectedValue(); 

        if (selectedTask != null) { 
            int id = selectedTask.getId(); 
            db.markAsDone(id);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih yang ingin ditandai selesai!");
        }
    }

    public void showDeadlinePopup(String taskTitle) {
        this.toFront();
        this.requestFocus();
        
        JOptionPane.showMessageDialog(this, 
            "Tugas: \"" + taskTitle + "\"\n\nWAKTU HABIS DEADLINE KELEWATAN AAAAAAAAAAAAAAAAAA🥶", 
            "🚨 DEADLINE LEWAT! 🚨", 
            JOptionPane.WARNING_MESSAGE); 
    }
    
    public void refreshTable() {
        Task selectedTask = taskList.getSelectedValue(); 
        listModel.clear(); 

        List<Task> list = db.getAllTasks();
        for (Task t : list) {
            listModel.addElement(t); 
        }
        
        if (selectedTask != null) {
            taskList.setSelectedValue(selectedTask, true);
        }
    }

    public void updateNotification(String text) {
        notifLabel.setText(text);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new ToDoAppGUI().setVisible(true));
    }
}