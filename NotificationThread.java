import java.util.List;
import java.util.Set; 
import java.util.HashSet; 
import javax.swing.SwingUtilities;

public class NotificationThread extends Thread {
    private DatabaseHelper db;
    private ToDoAppGUI gui;
    private Set<Integer> notifiedTaskIds;
    public NotificationThread(DatabaseHelper db, ToDoAppGUI gui) {
        this.db = db;
        this.gui = gui;
        this.notifiedTaskIds = new HashSet<>(); 
        setDaemon(true); 
        System.out.println("Mengecek task yang sudah lewat deadline saat start...");
        List<Task> initialTasks = db.getAllTasks();
        for (Task t : initialTasks) {
            if (!t.isDone() && t.getCountdown().startsWith("Waktu Habis!")) {
                notifiedTaskIds.add(t.getId());
            }
        }
        System.out.println("Ditemukan " + notifiedTaskIds.size() + " task yang sudah lewat (tidak akan di-popup).");
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<Task> tasks = db.getAllTasks();
                
                StringBuilder overdueNotif = new StringBuilder("🚨 PERINGATAN! Tugas lewat deadline:\n");
                int overdueCount = 0;

                for (Task t : tasks) {
                    if (!t.isDone() && t.getCountdown().startsWith("Waktu Habis!")) {
                        overdueNotif.append("👉 ").append(t.getTitle()).append("\n");
                        overdueCount++;
                        if (!notifiedTaskIds.contains(t.getId())) {
                            final String taskTitle = t.getTitle(); 
                            SwingUtilities.invokeLater(() -> {
                                gui.showDeadlinePopup(taskTitle);
                            });
                            notifiedTaskIds.add(t.getId());
                        }
                    }
                }

                String finalNotif;
                if (overdueCount > 0) {
                    finalNotif = overdueNotif.toString();
                } else {
                    finalNotif = "✅ Semua tugas aman, tidak ada yang lewat deadline.";
                }

                SwingUtilities.invokeLater(() -> {
                    gui.updateNotification(finalNotif);
                    gui.refreshTable(); 
                });

                Thread.sleep(1000); 
            } catch (InterruptedException e) {
                System.out.println("🔕 Thread notifikasi dihentikan.");
                break;
            }
        }
    }
}