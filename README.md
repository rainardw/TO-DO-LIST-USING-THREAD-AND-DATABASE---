# TO-DO-LIST-USING-THREAD-AND-DATABASE---
Aplikasi To-Do List 🌸 desktop dengan database MySQL. Fitur utama: countdown real-time, notifikasi pop-up, menggunakan thread dan database.



# 🌸 Rainy's To-Do List 🌸
<img width="979" height="783" alt="image" src="https://github.com/user-attachments/assets/ba5c3e5f-d809-43e0-9f59-dfd4799c0682" />

---

## Fitur Utama

* **GUI:** Tampilan daftar tugas kustom menggunakan `JList` dan `ListCellRenderer` menghindari tampilan tabel yang kaku.
* **Database MySQL:** Semua tugas disimpan, di-update, dan dibaca dari database MySQL menggunakan JDBC.
* **Multi-Threading:**
    1.  **Thread Utama (EDT):** Menangani semua interaksi GUI (klik tombol, input).
    2.  **Thread Notifikasi:** Berjalan di latar belakang, mengecek database setiap detik untuk memperbarui countdown dan memicu notifikasi.
* **Countdown Real-Time:** Setiap tugas memiliki sisa waktu yang menghitung mundur live di GUI.
* **Notifikasi Pop-up 🚨:** Saat countdown tugas mencapai 00:00:00, sebuah `JOptionPane` (pop-up) akan muncul secara otomatis untuk memberitahu pengguna.

---

## Teknologi yang Digunakan

* **Java**
* **Java Swing** (untuk komponen GUI)
* **MySQL** (untuk database)
* **MySQL Connector/J** (Driver JDBC)

---

## Penjelasan Struktur Kode

Proyek ini dibagi menjadi 4 kelas utama yang saling bekerja sama:

### 1. `DatabaseHelper.java`
Ini adalah kelas koneksi antara aplikasi Java dan database MySQL.
* **Fungsi:** Mengurus koneksi, `INSERT` (tambah tugas), `UPDATE` (tandai selesai), dan `SELECT` (ambil semua tugas).
* **Poin:** Menggunakan `Timestamp` untuk menyimpan tanggal dan waktu secara presisi ke kolom `DATETIME` di MySQL.

### 2. `Task.java`
Ini adalah data modelnya
* **Fungsi:** Mewakili satu buah tugas. Objek ini menyimpan `id`, `title`, `deadline` (sebagai `Timestamp`), dan `done`.
* **Poin:** Memiliki method `getCountdown()` yang menghitung selisih antara waktu sekarang dan `deadline` untuk ditampilkan di GUI.

### 3. `NotificationThread.java`
Ini adalah bagian yang bekerja di latar belakang.
* **Fungsi:** Berjalan di *thread* terpisah dalam satu *looping* tak terbatas (`while (true)`).
* **Poin:** Setiap 1 detik (`Thread.sleep(1000)`), ia akan:
    1.  Mengambil semua data tugas dari database (`db.getAllTasks()`).
    2.  Memerintahkan GUI untuk me-refresh tampilannya (`gui.refreshTable()`).
    3.  Mengecek apakah ada tugas yang waktunya habis.
    4.  Menggunakan `Set<Integer>` untuk melacak tugas mana yang sudah diberi pop-up, agar tidak spam pop-up setiap detik.
    5.  Memanggil `gui.showDeadlinePopup()` jika menemukan deadline yang *baru saja* terlewat.

### 4. `ToDoAppGUI.java`
Ini adalah kelas utama yang dilihat oleh pengguna (View dan Controller).
* **Fungsi:** Membangun `JFrame` dan semua komponen visual.
* **Poin:**
    * **Tidak pakai `JTable`:** Menggunakan `JList<Task>` yang diisi oleh `DefaultListModel<Task>`.
    * **`TaskListCellRenderer`:** Ini adalah *inner class* yang paling penting untuk tampilan. Kelas ini bertugas "menggambar" setiap objek `Task` di dalam `JList` menjadi sebuah `JPanel`, lengkap dengan pewarnaan (merah jika *overdue*, abu-abu jika *done*).
    * **`refreshTable()`:** Meskipun namanya "Table", method ini sebenarnya meng-update `JList`. Ia membersihkan `listModel`, mengisinya dengan data baru dari database, dan menjaga item yang dipilih tetap terpilih.
    * **`showDeadlinePopup()`:** Method yang dipanggil oleh `NotificationThread` untuk menampilkan `JOptionPane` peringatan.

---

## 5. Output
<img width="993" height="792" alt="Screenshot 2025-11-10 034104" src="https://github.com/user-attachments/assets/f298c4d7-fc7b-4acb-b1d5-4fd3f7537122" />
<img width="995" height="788" alt="Screenshot 2025-11-10 034125" src="https://github.com/user-attachments/assets/30664414-c6b1-4eee-8fef-b3a565d1f9a9" />
<img width="988" height="789" alt="Screenshot 2025-11-10 034145" src="https://github.com/user-attachments/assets/0f95c91d-5b14-41ea-9663-1d3032cc8c30" />
<img width="983" height="788" alt="Screenshot 2025-11-10 034415" src="https://github.com/user-attachments/assets/1f25a49a-8236-49af-adde-f0944e11e28f" />


