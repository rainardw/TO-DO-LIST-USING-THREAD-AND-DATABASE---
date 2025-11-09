import java.sql.Timestamp; 
import java.util.concurrent.TimeUnit;

public class Task {
    private int id;
    private String title;
    private Timestamp deadline; 
    private boolean done;
    public Task(int id, String title, Timestamp deadline, boolean done) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.done = done;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public Timestamp getDeadline() { return deadline; }
    public boolean isDone() { return done; }
    public String getCountdown() {
        if (isDone()) {
            return "Selesai ✅";
        }
        long diffInMillis = deadline.getTime() - System.currentTimeMillis();
        if (diffInMillis <= 0) {
            return "Waktu Habis! ❌";
        }

        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis) % 60;

        if (days > 0) {
            return String.format("%d hari, %02d:%02d:%02d", days, hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    @Override
    public String toString() {
        return id + ". " + title + " (Sisa Waktu: " + getCountdown() + ")" + (done ? " ✅" : " ⏳");
    }
}