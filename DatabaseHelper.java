import java.sql.*; 
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private Connection conn;

    public DatabaseHelper() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/todo_db",
                "root", ""
            );
            System.out.println("✅ Koneksi ke database berhasil!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Gagal konek ke database!");
        }
    }

    public void addTask(String title, String deadline) {
        String sql = "INSERT INTO tasks (title, deadline, done) VALUES (?, ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setTimestamp(2, Timestamp.valueOf(deadline)); 
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("Format tanggal/waktu salah. Harusnya YYYY-MM-DD HH:MM:SS");
        }
    }

    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY id DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getTimestamp("deadline"), 
                    rs.getBoolean("done")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void markAsDone(int id) {
        String sql = "UPDATE tasks SET done = 1 WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}