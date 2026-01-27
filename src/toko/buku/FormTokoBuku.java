/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toko.buku;

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrator
 */
public class FormTokoBuku extends javax.swing.JFrame {

    /**
     * Creates new form FormTokoBuku
     */
    
    private String generateNoFaktur() {
    Connection conn = koneksi.getConnection();
    String noFaktur = null;
    
    if (conn != null) {
        try {
            // Ambil nomor faktur terakhir
            String sqlSelect = "SELECT No_Faktur_Akhir FROM tbl_nofak LIMIT 1";
            PreparedStatement psSelect = conn.prepareStatement(sqlSelect);
            ResultSet rs = psSelect.executeQuery();
            
            int nextNumber = 1; // Default mulai dari 1
            if (rs.next()) {
                nextNumber = rs.getInt("No_Faktur_Akhir") + 1;
            }
            rs.close();
            psSelect.close();
            
            // Update nomor faktur terakhir
            String sqlUpdate = "INSERT INTO tbl_nofak (No_Faktur_Akhir) VALUES (?) ON DUPLICATE KEY UPDATE No_Faktur_Akhir = ?";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
            psUpdate.setInt(1, nextNumber);
            psUpdate.setInt(2, nextNumber);
            psUpdate.executeUpdate();
            psUpdate.close();
            
            // Format sebagai F-0000
            noFaktur = String.format("F-%04d", nextNumber);
            
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    return noFaktur;
}
    
    private boolean hapusTransaksi(String noFaktur) {

    Connection conn = koneksi.getConnection();

    if (conn != null) {
        try {
            String sql = "DELETE FROM transaksi WHERE No_Faktur = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, noFaktur);

            int hasil = ps.executeUpdate();

            ps.close();
            conn.close();

            return hasil > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    return false;
}

    
    private boolean updateTransaksi(String noFaktur, String isbn, int jumlah, long total) {

    Connection conn = koneksi.getConnection();

    if (conn != null) {
        try {
            String sql = "UPDATE transaksi SET ISBN=?, Jumlah=?, Total=? WHERE No_Faktur=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, isbn);
            ps.setInt(2, jumlah);
            ps.setLong(3, total);
            ps.setString(4, noFaktur);

            int hasil = ps.executeUpdate();

            ps.close();
            conn.close();

            return hasil > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    return false;
}

    
    private boolean simpanTransaksi(String noFaktur, Date tanggal, String isbn, int jumlah, long total) {

    Connection conn = koneksi.getConnection();

    if (conn != null) {
        try {
            String sql = "INSERT INTO transaksi (No_Faktur, Tanggal, ISBN, Jumlah, Total) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, noFaktur);
            ps.setDate(2, new java.sql.Date(tanggal.getTime()));
            ps.setString(3, isbn);
            ps.setInt(4, jumlah);
            ps.setLong(5, total);

            int hasil = ps.executeUpdate();

            ps.close();
            conn.close();

            return hasil > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    return false;
}

    
    private void loadCoverBuku(String path){
        ImageIcon gambar = new ImageIcon(getClass().getResource(path));
        
        Image img = gambar.getImage();
        Image resizedImg = img.getScaledInstance(100, 150, Image.SCALE_SMOOTH);
        
        gambar = new ImageIcon(resizedImg);
        
        coverBuku.setIcon(gambar);
    }
    
    private void pencarianBuku() {
    String ISBN = fieldISBN.getText();
    Connection conn = koneksi.getConnection();

    if (conn != null) {
        try {
            String sql = "SELECT ISBN, Judul, Harga, Photo_Cover FROM buku WHERE ISBN = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ISBN);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("ISBN : " + rs.getString("ISBN"));

                fieldJudul.setText(rs.getString("Judul"));
                fieldHarga.setText(rs.getString("Harga"));
                loadCoverBuku(rs.getString("Photo_Cover"));
            } else {
                fieldJudul.setText("Buku tidak ditemukan!");
                fieldHarga.setText("0");
                loadCoverBuku("assets/autoBooks_white_100.png");
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

    private void loadData() {
    Connection conn = koneksi.getConnection();

    if (conn != null) {
        try {
            String sql = "SELECT t.No_Faktur, t.Tanggal, t.ISBN, b.Judul, t.Jumlah, b.Harga, t.Total "
                +"FROM transaksi t "
                +"INNER JOIN buku b ON t.ISBN = b.ISBN";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tableDataPembelian.getModel();
            model.setRowCount(0); // bersihkan data lama

            while (rs.next()) {
                Object[] row = {
                    rs.getString("No_Faktur"),
                    rs.getDate("Tanggal"),
                    rs.getString("ISBN"),
                    rs.getString("Judul"),
                    rs.getInt("Jumlah"),
                    rs.getLong("Harga"),  // Changed to getLong for consistency
                    rs.getLong("Total")   // Changed to getLong for consistency
                };

                model.addRow(row);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    private long hitungTotal() {
    int jumlah = (int) spinnerJumlah.getValue();
    int diskon = (int) spinnerDiskon.getValue(); // persen
    long harga = Long.parseLong(fieldHarga.getText());

    long subtotal = jumlah * harga;
    long potongan = subtotal * diskon / 100;
    long total = subtotal - potongan;

    return total;
}
    
    private void bersihkanForm() {
    fieldNoFaktur.setText("");
    fieldISBN.setText("");
    fieldJudul.setText("");
    fieldHarga.setText("");

    spinnerJumlah.setValue(1);
    spinnerDiskon.setValue(0);
    spinnerTotal.setValue(0L);
    spinnerBayar.setValue(0L);
    spinnerKembalian.setValue(0L);

    fieldTanggal.setDate(new Date()); // set ke hari ini

    tableDataPembelian.clearSelection();

    fieldNoFaktur.setEditable(true);

    tombolSave.setEnabled(true);
    tombolEdit.setEnabled(false);
    tombolDelete.setEnabled(false);
}

    
    private void updateTotal() {
    long total = hitungTotal();
    spinnerTotal.setValue(total);
    // Also update kembalian if bayar is set
}

    private void updateKembalian() {
    long total = (Long) spinnerTotal.getValue();
    long bayar = (Long) spinnerBayar.getValue();
    long kembalian = bayar - total;
    spinnerKembalian.setValue(kembalian);
}

 
    public FormTokoBuku() {
        initComponents();
        // Set spinner models to handle long values
        spinnerJumlah.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        spinnerDiskon.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        spinnerTotal.setModel(new SpinnerNumberModel(0L, 0L, Long.MAX_VALUE, 1L));
        spinnerBayar.setModel(new SpinnerNumberModel(0L, 0L, Long.MAX_VALUE, 1L));
        spinnerKembalian.setModel(new SpinnerNumberModel(0L, Long.MIN_VALUE, Long.MAX_VALUE, 1L));

        LocalDate today = LocalDate.now();
        Date date = Date.from(
        today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        fieldTanggal.setDate(date);
        loadData();
        fieldISBN.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                pencarianBuku();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                pencarianBuku();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                pencarianBuku();
            }
            
        });
        spinnerJumlah.addChangeListener(e -> updateTotal());
        spinnerDiskon.addChangeListener(e -> updateTotal());
        spinnerBayar.addChangeListener(e -> updateKembalian());

        tombolNewTransaction.addActionListener(e -> bersihkanForm());

        tableDataPembelian.addMouseListener(new MouseAdapter() {
           @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableDataPembelian.getSelectedRow();

                if (row != -1) {
                fieldNoFaktur.setText(tableDataPembelian.getValueAt(row, 0).toString());
                fieldTanggal.setDate((Date) tableDataPembelian.getValueAt(row, 1));
                fieldISBN.setText(tableDataPembelian.getValueAt(row, 2).toString());
                fieldJudul.setText(tableDataPembelian.getValueAt(row, 3).toString());
                spinnerJumlah.setValue(Integer.parseInt(
                tableDataPembelian.getValueAt(row, 4).toString()
                ));
                fieldHarga.setText(tableDataPembelian.getValueAt(row, 5).toString());
            spinnerTotal.setValue(Long.parseLong(
                tableDataPembelian.getValueAt(row, 6).toString()
            ));
        }
    }
});
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableDataPembelian = new javax.swing.JTable();
        tombolExit = new javax.swing.JButton();
        tombolNewTransaction = new javax.swing.JButton();
        tombolSave = new javax.swing.JButton();
        tombolEdit = new javax.swing.JButton();
        tombolDelete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fieldNoFaktur = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        coverBuku = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        fieldISBN = new javax.swing.JTextField();
        fieldJudul = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        fieldHarga = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        spinnerJumlah = new javax.swing.JSpinner();
        spinnerDiskon = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        spinnerTotal = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        spinnerBayar = new javax.swing.JSpinner();
        spinnerKembalian = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        tombolGenerate = new javax.swing.JButton();
        fieldTanggal = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 28, 48));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(218, 255, 251));
        jLabel1.setText("Data Pembelian :");

        tableDataPembelian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Faktur", "Tanggal", "ISBN", "Judul Buku", "Jumlah", "Harga", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tableDataPembelian);

        tombolExit.setBackground(new java.awt.Color(23, 107, 135));
        tombolExit.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tombolExit.setForeground(new java.awt.Color(218, 255, 251));
        tombolExit.setText("Exit");
        tombolExit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tombolExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tombolExitActionPerformed(evt);
            }
        });

        tombolNewTransaction.setBackground(new java.awt.Color(23, 107, 135));
        tombolNewTransaction.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tombolNewTransaction.setForeground(new java.awt.Color(218, 255, 251));
        tombolNewTransaction.setText("New Transaction");
        tombolNewTransaction.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        tombolSave.setBackground(new java.awt.Color(23, 107, 135));
        tombolSave.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tombolSave.setForeground(new java.awt.Color(218, 255, 251));
        tombolSave.setText("Save");
        tombolSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tombolSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tombolSaveActionPerformed(evt);
            }
        });

        tombolEdit.setBackground(new java.awt.Color(23, 107, 135));
        tombolEdit.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tombolEdit.setForeground(new java.awt.Color(218, 255, 251));
        tombolEdit.setText("Edit");
        tombolEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tombolEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tombolEditActionPerformed(evt);
            }
        });

        tombolDelete.setBackground(new java.awt.Color(23, 107, 135));
        tombolDelete.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        tombolDelete.setForeground(new java.awt.Color(218, 255, 251));
        tombolDelete.setText("Delete");
        tombolDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tombolDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tombolDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tombolExit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tombolNewTransaction)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tombolSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tombolEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tombolDelete))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tombolExit)
                    .addComponent(tombolNewTransaction)
                    .addComponent(tombolSave)
                    .addComponent(tombolEdit)
                    .addComponent(tombolDelete))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 690, 500));

        jPanel2.setBackground(new java.awt.Color(23, 107, 135));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(218, 255, 251));
        jLabel2.setText("Transaksi :");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(218, 255, 251));
        jLabel3.setText("No. Faktur");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(218, 255, 251));
        jLabel4.setText("Tanggal");

        fieldNoFaktur.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        fieldNoFaktur.setMargin(new java.awt.Insets(4, 8, 4, 8));

        jPanel3.setBackground(new java.awt.Color(0, 28, 48));

        coverBuku.setBackground(new java.awt.Color(51, 255, 204));
        coverBuku.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        coverBuku.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toko/buku/assets/autoBooks_white_100.png"))); // NOI18N
        coverBuku.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(218, 255, 251), 1, true));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(218, 255, 251));
        jLabel7.setText("Data Buku");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(218, 255, 251));
        jLabel8.setText("ISBN");

        fieldISBN.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        fieldISBN.setText("22345678");

        fieldJudul.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(218, 255, 251));
        jLabel9.setText("Judul");

        fieldHarga.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(218, 255, 251));
        jLabel10.setText("Harga");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fieldJudul, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(fieldISBN, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fieldHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10)))
                .addComponent(coverBuku, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(coverBuku, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(fieldISBN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(fieldJudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(fieldHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(5, 5, 5))
        );

        jPanel4.setBackground(new java.awt.Color(0, 28, 48));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(218, 255, 251));
        jLabel11.setText("Transaksi");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(218, 255, 251));
        jLabel12.setText("Jumlah");

        spinnerJumlah.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        spinnerDiskon.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(218, 255, 251));
        jLabel13.setText("Diskon %");

        spinnerTotal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(218, 255, 251));
        jLabel14.setText("Total Bayar");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(218, 255, 251));
        jLabel15.setText("Bayar");

        spinnerBayar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        spinnerKembalian.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(218, 255, 251));
        jLabel16.setText("Kembalian");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spinnerTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinnerBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinnerKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(spinnerDiskon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                                .addComponent(spinnerJumlah, javax.swing.GroupLayout.Alignment.LEADING)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(spinnerJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(spinnerDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(spinnerTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(spinnerBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(spinnerKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setForeground(new java.awt.Color(218, 255, 251));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Copyright© January 2026 - RandyBGN");

        tombolGenerate.setBackground(new java.awt.Color(0, 28, 48));
        tombolGenerate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/toko/buku/assets/addDiamon_white_25.png"))); // NOI18N
        tombolGenerate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tombolGenerate.setMargin(new java.awt.Insets(0, 0, 0, 0));
        tombolGenerate.setMaximumSize(new java.awt.Dimension(40, 40));
        tombolGenerate.setMinimumSize(new java.awt.Dimension(40, 40));
        tombolGenerate.setPreferredSize(new java.awt.Dimension(40, 40));
        tombolGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tombolGenerateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fieldNoFaktur)
                            .addComponent(fieldTanggal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tombolGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel5)))
                .addGap(30, 30, 30))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(8, 8, 8))
                    .addComponent(fieldNoFaktur, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                    .addComponent(tombolGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(fieldTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 0, 360, 500));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tombolGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tombolGenerateActionPerformed
        // TODO add your handling code here:
        String noFaktur = generateNoFaktur();
    if (noFaktur != null) {
        fieldNoFaktur.setText(noFaktur);
    } else {
        JOptionPane.showMessageDialog(this, "Gagal generate nomor faktur!");
    }
    }//GEN-LAST:event_tombolGenerateActionPerformed

    private void tombolExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tombolExitActionPerformed
        // TODO add your handling code here:
        int plh = JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin keluar", "Keluar", JOptionPane.YES_NO_OPTION);
        if(plh == JOptionPane.YES_OPTION){
        System.exit(0);
        }
    }//GEN-LAST:event_tombolExitActionPerformed

    private void tombolSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tombolSaveActionPerformed
        // TODO add your handling code here:
        try {
        String noFaktur = fieldNoFaktur.getText();
        String isbn = fieldISBN.getText();
        int jumlah = (int) spinnerJumlah.getValue();
        long total = (Long) spinnerTotal.getValue();
        Date tanggal = fieldTanggal.getDate(); // tanggal hari ini

        if (noFaktur.isEmpty() || isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No Faktur dan ISBN wajib diisi!");
            return;
        }

        boolean berhasil = simpanTransaksi(noFaktur, tanggal, isbn, jumlah, total);

        if (berhasil) {
            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!");
            loadData(); // refresh JTable
            bersihkanForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi!");
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Jumlah dan Total harus angka!");
    }
    }//GEN-LAST:event_tombolSaveActionPerformed

    private void tombolEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tombolEditActionPerformed
        // TODO add your handling code here:
        try {
        String noFaktur = fieldNoFaktur.getText();
        String isbn = fieldISBN.getText();
        int jumlah = (int) spinnerJumlah.getValue();
        long total = (long) spinnerTotal.getValue();

        boolean berhasil = updateTransaksi(noFaktur, isbn, jumlah, total);

        if (berhasil) {
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal update data!");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Input tidak valid!");
    }

    }//GEN-LAST:event_tombolEditActionPerformed

    private void tombolDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tombolDeleteActionPerformed
        // TODO add your handling code here:
        int row = tableDataPembelian.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu!");
        return;
    }

    String noFaktur = fieldNoFaktur.getText();

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "Yakin ingin menghapus transaksi " + noFaktur + "?",
        "Konfirmasi Hapus",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {

        boolean berhasil = hapusTransaksi(noFaktur);

        if (berhasil) {
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
            loadData();
            bersihkanForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data!");
        }
    }

    }//GEN-LAST:event_tombolDeleteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormTokoBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormTokoBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormTokoBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormTokoBuku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormTokoBuku().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel coverBuku;
    private javax.swing.JTextField fieldHarga;
    private javax.swing.JTextField fieldISBN;
    private javax.swing.JTextField fieldJudul;
    private javax.swing.JTextField fieldNoFaktur;
    private com.toedter.calendar.JDateChooser fieldTanggal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner spinnerBayar;
    private javax.swing.JSpinner spinnerDiskon;
    private javax.swing.JSpinner spinnerJumlah;
    private javax.swing.JSpinner spinnerKembalian;
    private javax.swing.JSpinner spinnerTotal;
    private javax.swing.JTable tableDataPembelian;
    private javax.swing.JButton tombolDelete;
    private javax.swing.JButton tombolEdit;
    private javax.swing.JButton tombolExit;
    private javax.swing.JButton tombolGenerate;
    private javax.swing.JButton tombolNewTransaction;
    private javax.swing.JButton tombolSave;
    // End of variables declaration//GEN-END:variables
}
