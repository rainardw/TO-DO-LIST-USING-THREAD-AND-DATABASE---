import javax.swing.*;
import javax.swing.border.Border; // Import baru
import javax.swing.table.DefaultTableModel; // Ini masih dipakai di kode lama, tapi kita hapus
import java.awt.*;
import java.util.List;

// Import baru untuk JList
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

public class ToDoAppGUI extends JFrame {
    private DatabaseHelper db;
    // private JTable table; // UBAH: Dibuang
    // private DefaultTableModel model; // UBAH: Dibuang
    
    // PENGGANTI JTable
    private JList<Task> taskList;
    private DefaultListModel<Task> listModel;

    private JTextArea notifLabel;

    // --- WARNA DAN FONT KUSTOM ---
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

        // --- UBAH: Inisialisasi JList ---
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // --- INI AJAIBNYA: Pakai Renderer kustom ---
        taskList.setCellRenderer(new TaskListCellRenderer());
        
        // Atur agar JList tidak punya background (renderer yang akan urus)
        taskList.setBackground(colorBg); 

        JScrollPane scroll = new JScrollPane(taskList);
        scroll.setBorder(BorderFactory.createLineBorder(colorHeader));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // List hanya vertikal

        // --- Tombol (sama) ---
        JButton btnAdd = new JButton("Tambah To-Do List");
        JButton btnDone = new JButton("Yeay! Selesai gu😁😁😁");
        btnAdd.setFont(fontDetail);
        btnDone.setFont(fontDetail);

        // --- Notifikasi (sama) ---
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

        // --- Panel Tombol (sama) ---
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(colorPanel);
        panel.add(btnAdd);
        panel.add(btnDone);

        // --- Add ke Frame ---
        add(notifScroll, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER); // Ini sekarang berisi JList
        add(panel, BorderLayout.SOUTH);

        // --- Listener (sama) ---
        btnAdd.addActionListener(e -> tambahTugas());
        btnDone.addActionListener(e -> tandaiSelesai()); // Ini akan kita ubah sedikit

        new NotificationThread(db, this).start();
    }


    // --- KELAS INTERNAL BARU: Renderer untuk JList ---
    // Ini adalah 'pabrik' yang menggambar setiap item di JList
    class TaskListCellRenderer extends JPanel implements ListCellRenderer<Task> {
        
        private JLabel lblJudul = new JLabel();
        private JLabel lblDeadline = new JLabel();
        private JLabel lblSisaWaktu = new JLabel();
        private JLabel lblStatus = new JLabel();
        
        private Border defaultBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, colorHeader), // Garis bawah tipis
            BorderFactory.createEmptyBorder(10, 15, 10, 15) // Padding
        );
        private Border selectedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 1, 0, Color.BLUE), // Garis kiri biru
            BorderFactory.createEmptyBorder(10, 10, 10, 15) // Padding
        );

        // Warna dari renderer tabel sebelumnya
        private final Color colorDoneText = new Color(150, 150, 150);
        private final Color colorOverdueBg = new Color(255, 230, 230);
        private final Color colorOverdueText = new Color(180, 0, 0);
        private final Color colorSelected = new Color(230, 240, 255); // Biru seleksi muda

        public TaskListCellRenderer() {
            setLayout(new BorderLayout(15, 0)); // layout utama (teks, status)
            
            // Panel untuk teks (Judul, Deadline, Sisa Waktu)
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false); // Transparan

            lblJudul.setFont(fontJudul);
            lblDeadline.setFont(fontDetail);
            lblSisaWaktu.setFont(fontCountdown);
            lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 24)); // Emoji
            
            textPanel.add(lblJudul);
            textPanel.add(Box.createVerticalStrut(5)); // Spasi
            textPanel.add(lblDeadline);
            textPanel.add(lblSisaWaktu);

            add(textPanel, BorderLayout.CENTER);
            add(lblStatus, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Task> list, Task task,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            
            // 1. Ambil data dari Objek Task
            String status = task.isDone() ? "✅" : "⏳";
            String sisaWaktu = task.getCountdown();

            // 2. Set Teks
            lblJudul.setText(task.getTitle());
            lblDeadline.setText("Deadline: " + task.getDeadline().toString().replace(".0", ""));
            lblSisaWaktu.setText("Sisa Waktu: " + sisaWaktu);
            lblStatus.setText(status);

            // 3. Atur Warna Teks
            lblJudul.setForeground(Color.BLACK);
            lblDeadline.setForeground(Color.DARK_GRAY);
            lblSisaWaktu.setForeground(Color.BLACK);
            
            // 4. Atur Warna Background Panel
            if (task.isDone()) { // Selesai
                setBackground(new Color(248, 248, 248)); // Abu-abu sangat muda
                lblJudul.setForeground(colorDoneText);
                lblDeadline.setForeground(colorDoneText);
                lblSisaWaktu.setForeground(colorDoneText);
            } else if (sisaWaktu.startsWith("Waktu Habis!")) { // Lewat Deadline
                setBackground(colorOverdueBg);
                lblJudul.setForeground(colorOverdueText);
                lblSisaWaktu.setForeground(colorOverdueText);
            } else { // Normal
                setBackground(Color.WHITE);
            }

            // 5. Atur Warna Seleksi
            if (isSelected) {
                setBackground(colorSelected);
                setBorder(selectedBorder);
            } else {
                setBorder(defaultBorder);
            }

            return this; // Kembalikan panel ini untuk digambar
        }
    }
    // --- SELESAI KELAS RENDERER ---


    private void tambahTugas() {
        // --- METHOD INI SAMA PERSIS, TIDAK BERUBAH ---
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

    // --- UBAH: Logika tandaiSelesai() disesuaikan untuk JList ---
    private void tandaiSelesai() {
        // int row = table.getSelectedRow(); // Dibuang
        Task selectedTask = taskList.getSelectedValue(); // Ambil Objek Task yang dipilih

        // if (row >= 0) { // Dibuang
        if (selectedTask != null) { // Ganti cek-nya
            // int id = (int) model.getValueAt(row, 0); // Dibuang
            int id = selectedTask.getId(); // Langsung ambil ID dari objek
            db.markAsDone(id);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih yang ingin ditandai selesai!");
        }
    }

    public void showDeadlinePopup(String taskTitle) {
        // --- METHOD INI SAMA PERSIS, TIDAK BERUBAH ---
        this.toFront();
        this.requestFocus();
        
        JOptionPane.showMessageDialog(this, 
            "Tugas: \"" + taskTitle + "\"\n\nWAKTU HABIS DEADLINE KELEWATAN AAAAAAAAAAAAAAAAAA🥶", 
            "🚨 DEADLINE LEWAT! 🚨", 
            JOptionPane.WARNING_MESSAGE); 
    }
    
    // --- UBAH: Logika refreshTable() disesuaikan untuk JList ---
    // PENTING: Nama method tetap 'refreshTable()' agar NotificationThread tidak error
    public void refreshTable() {
        // int selectedRow = table.getSelectedRow(); // Dibuang
        Task selectedTask = taskList.getSelectedValue(); // Simpan task yang dipilih
        
        // model.setRowCount(0); // Dibuang
        listModel.clear(); // Hapus semua item di list model

        List<Task> list = db.getAllTasks();
        for (Task t : list) {
            // model.addRow(...); // Dibuang
            listModel.addElement(t); // Tambahkan objek Task ke list model
        }
        
        // Kembalikan seleksi
        if (selectedTask != null) {
            taskList.setSelectedValue(selectedTask, true);
        }
    }

    public void updateNotification(String text) {
        // --- METHOD INI SAMA PERSIS, TIDAK BERUBAH ---
        notifLabel.setText(text);
    }

    public static void main(String[] args) {
        // --- METHOD INI SAMA PERSIS, TIDAK BERUBAH ---
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new ToDoAppGUI().setVisible(true));
    }
}