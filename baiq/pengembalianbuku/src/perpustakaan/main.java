/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perpustakaan;

import java.awt.Image;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrator
 */
public class main extends javax.swing.JFrame {

    private void updateCover(String path){
        if(!path.isEmpty()){
            ImageIcon coverIcon = new ImageIcon(path);
            Image image = coverIcon.getImage().getScaledInstance(100, 130, Image.SCALE_SMOOTH);
            iconCover.setIcon(new ImageIcon(image));
        }else{
            URL imgURL = getClass().getResource("/perpustakaan/covers/icon.png");
            if(imgURL != null){
                ImageIcon defaultIcon = new ImageIcon(imgURL);
                Image defaultImg = defaultIcon.getImage().getScaledInstance(100, 130, Image.SCALE_SMOOTH);
                iconCover.setIcon(new ImageIcon(defaultImg));
            }
        }
    }
    
    private boolean isKodePinjamExist(int kode) {
    for(Object obj : transaksi) {
        Object[] dataTransaksi = (Object[]) obj;
        int kodeData = (int) dataTransaksi[0];
        if(kodeData == kode) {
            return true;
        }
    }
    return false;
}
    
    private void updateBuku(){
        if(fieldISBN.getText().isEmpty()) return;
        try {
            int kodeISBN = Integer.parseInt(fieldISBN.getText());
            boolean ditemukan = false;
            for(Object obj : data_buku){
                Object[] dataBuku = (Object[]) obj;
                int ISBN = (int) dataBuku[0];
                if(kodeISBN == ISBN){
                    fieldJudul.setText((String) dataBuku[1]);
                    fieldPenerbit.setText((String) dataBuku[2]);
                    fieldTahunTerbit.setText((String) dataBuku[3]);
                    updateCover((String) dataBuku[4]);
                    ditemukan = true;
                    break;
                }
            }
            if(!ditemukan){
                fieldJudul.setText("Buku Tidak Ditemukan");
                fieldPenerbit.setText("");
                fieldTahunTerbit.setText("");
                updateCover("");
            }
        } catch(NumberFormatException e) {}
    }
    
    private void loadTable(){
        DefaultTableModel model = (DefaultTableModel) transaksiTable.getModel();
        model.setRowCount(0);
        for(Object obj : transaksi){
            Object[] row = (Object[]) obj;
            model.addRow(new Object[]{
                row[0], row[1], row[2], row[3], row[4], row[5]
            });
        }
    }
    
    private void clearForm(){
    fieldKodePinjam.setText("");
    fieldNamaAnggota.setText("");
    fieldJumlah.setText("");
    fieldISBN.setText("");
    fieldJudul.setText("");
    fieldPenerbit.setText("");
    fieldTahunTerbit.setText("");
    datePinjam.setDate(null);
    dateKembalikan.setDate(null);
    updateCover("");
    fieldKodePinjam.setEnabled(true);
}
    
    private void simpanTransaksi() {
    Object[] transaksiLama = transaksi;
    transaksi = new Object[transaksiLama.length + 1];
    System.arraycopy(transaksiLama, 0, transaksi, 0, transaksiLama.length);
    transaksi[transaksiLama.length] = transaksiSementara;
    
    loadTable();
    statusMeminjam = false;
    jumlahBukuPinjam = 0;
    counterInputBuku = 0;
    transaksiSementara = null;
    clearForm();
    fieldKodePinjam.setEnabled(true);
    
    JOptionPane.showMessageDialog(this, 
        "Transaksi berhasil disimpan!",
        "Sukses", JOptionPane.INFORMATION_MESSAGE);
}
    
    private void tambahBukuKeTemp(int isbn, String judul, String penerbit, String tahun, String cover) {
    if(tempPilihBuku.size() >= 2) {
        JOptionPane.showMessageDialog(this, "Maksimal 2 buku per peminjaman!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    Object[] buku = new Object[]{
        isbn, judul, penerbit, tahun, cover
    };
    tempPilihBuku.add(buku);
    
    String daftarBuku = "";
    for(int i = 0; i < tempPilihBuku.size(); i++) {
        Object[] b = tempPilihBuku.get(i);
        daftarBuku += (i+1) + ". " + b[1] + " (ISBN: " + b[0] + ")\n";
    }
    
    JOptionPane.showMessageDialog(this, "Buku ditambahkan!\n\nDaftar Buku Dipilih:\n" + daftarBuku, 
        "Keranjang Buku", JOptionPane.INFORMATION_MESSAGE);
    
    fieldJumlah.setText(String.valueOf(tempPilihBuku.size()));
}
    
    Object[] data_buku ={
    new Object[] {11110, "Ancika", "Gramedia", "2026-10-13", "src/covers/ancika.jpg"},
    new Object[] {11111, "kalaitu", "Gramedia", "2026-10-13", "src/covers/kalaitu.jpg"},
};

Object[] transaksi ={
    new Object[] {22220, "2026-08-11", "Ancika", 11110, 11111, "2026-08-11", 5000},
};

private int jumlahBukuPinjam = 0;
private int counterInputBuku = 0;
private boolean statusMeminjam = false;
private Object[] transaksiSementara = null;

private java.util.ArrayList<Object[]> tempPilihBuku = new java.util.ArrayList<>();

        
    public main() {
        initComponents();
        loadTable();
        
        fieldISBN.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e) { updateBuku(); }
            public void removeUpdate(DocumentEvent e) { updateBuku(); }
            public void changedUpdate(DocumentEvent e) { updateBuku(); }
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
        panelPinjam = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        fieldNamaAnggota = new javax.swing.JTextField();
        fieldJumlah = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        datePinjam = new com.toedter.calendar.JDateChooser();
        pinjamButton = new javax.swing.JButton();
        panelPilihBuku = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        fieldISBN = new javax.swing.JTextField();
        fieldJudul = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        fieldPenerbit = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        fieldTahunTerbit = new javax.swing.JTextField();
        iconCover = new javax.swing.JLabel();
        pilihBukuButton = new javax.swing.JButton();
        texit = new javax.swing.JButton();
        panelPengembalian = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        kembalikanButton = new javax.swing.JButton();
        dateKembalikan = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        transaksiTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        fieldKodePinjam = new javax.swing.JTextField();
        cekKodeButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(11, 45, 114));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelPinjam.setBackground(new java.awt.Color(9, 146, 194));
        panelPinjam.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelPinjam.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setForeground(new java.awt.Color(255, 253, 241));
        jLabel3.setText("Tanggal Pinjam");
        panelPinjam.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, -1, -1));

        jLabel4.setForeground(new java.awt.Color(255, 253, 241));
        jLabel4.setText("Nama Anggota");
        panelPinjam.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 37, -1, -1));
        panelPinjam.add(fieldNamaAnggota, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 34, 183, -1));
        panelPinjam.add(fieldJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 62, 183, -1));

        jLabel5.setForeground(new java.awt.Color(255, 253, 241));
        jLabel5.setText("Jumlah Buku");
        panelPinjam.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 65, -1, -1));
        panelPinjam.add(datePinjam, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 6, 183, -1));

        pinjamButton.setText("Pinjam");
        pinjamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pinjamButtonActionPerformed(evt);
            }
        });
        panelPinjam.add(pinjamButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(254, 95, -1, -1));

        jPanel1.add(panelPinjam, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 10, 430, 120));

        panelPilihBuku.setBackground(new java.awt.Color(9, 146, 194));
        panelPilihBuku.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelPilihBuku.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setForeground(new java.awt.Color(255, 253, 241));
        jLabel7.setText("ISBN");
        panelPilihBuku.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 32, -1, -1));
        panelPilihBuku.add(fieldISBN, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 29, 240, -1));
        panelPilihBuku.add(fieldJudul, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 63, 240, -1));

        jLabel8.setForeground(new java.awt.Color(255, 253, 241));
        jLabel8.setText("Judul");
        panelPilihBuku.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 66, -1, -1));

        jLabel9.setForeground(new java.awt.Color(255, 253, 241));
        jLabel9.setText("Penerbit");
        panelPilihBuku.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 105, -1, -1));
        panelPilihBuku.add(fieldPenerbit, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 97, 240, -1));

        jLabel10.setForeground(new java.awt.Color(255, 253, 241));
        jLabel10.setText("Tahun Terbit");
        panelPilihBuku.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 134, -1, -1));
        panelPilihBuku.add(fieldTahunTerbit, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 131, 240, -1));

        iconCover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/covers/icon.png"))); // NOI18N
        iconCover.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelPilihBuku.add(iconCover, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, 100, 130));

        pilihBukuButton.setText("Pilih Buku");
        pilihBukuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihBukuButtonActionPerformed(evt);
            }
        });
        panelPilihBuku.add(pilihBukuButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 310, -1, -1));

        texit.setText("Exit");
        texit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texitActionPerformed(evt);
            }
        });
        panelPilihBuku.add(texit, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, -1, -1));

        jPanel1.add(panelPilihBuku, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 360, 380));

        panelPengembalian.setBackground(new java.awt.Color(9, 146, 194));
        panelPengembalian.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panelPengembalian.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setForeground(new java.awt.Color(255, 253, 241));
        jLabel13.setText("Tanggal pengembalian");
        panelPengembalian.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 21, -1, -1));

        kembalikanButton.setText("Kembalikan");
        kembalikanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kembalikanButtonActionPerformed(evt);
            }
        });
        panelPengembalian.add(kembalikanButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(233, 57, -1, -1));
        panelPengembalian.add(dateKembalikan, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 15, 185, -1));

        jPanel1.add(panelPengembalian, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 140, 430, 90));

        transaksiTable.setBackground(new java.awt.Color(9, 146, 194));
        transaksiTable.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        transaksiTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Transaksi", "Tanggal Pinjam", "Nama Anggota", "ISBN 1", "ISBN 2", "Tanggal Kembali"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(transaksiTable);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 240, 430, 280));

        jPanel2.setBackground(new java.awt.Color(9, 146, 194));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setForeground(new java.awt.Color(255, 253, 241));
        jLabel2.setText("Kode Pinjam");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 370, -1, -1));
        jPanel2.add(fieldKodePinjam, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, 180, -1));

        cekKodeButton.setText("Cek");
        cekKodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cekKodeButtonActionPerformed(evt);
            }
        });
        jPanel2.add(cekKodeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, -1, -1));

        jLabel1.setForeground(new java.awt.Color(255, 253, 241));
        jLabel1.setText("Cek Kode");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 360, 120));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 870, 570));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cekKodeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cekKodeButtonActionPerformed

    String kodeStr = fieldKodePinjam.getText().trim();
    if(kodeStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Masukkan kode pinjam!", 
            "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    try {
        int kode = Integer.parseInt(kodeStr);
        
        if(isKodePinjamExist(kode)) {
            JOptionPane.showMessageDialog(this, "Kode Pinjam SUDAH digunakan!", 
                "Hasil Cek", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Kode Pinjam TERSEDIA!", 
                "Hasil Cek", JOptionPane.INFORMATION_MESSAGE);
        }
        
    } catch(NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Kode Pinjam harus angka!", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }

    }//GEN-LAST:event_cekKodeButtonActionPerformed

    private void pilihBukuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihBukuButtonActionPerformed
if(!statusMeminjam) {
    JOptionPane.showMessageDialog(this, "Tekan tombol Pinjam terlebih dahulu!", 
        "Peringatan", JOptionPane.WARNING_MESSAGE);
    return;
}

if(counterInputBuku >= jumlahBukuPinjam) {
    JOptionPane.showMessageDialog(this, "Semua buku sudah diinput!", 
        "Informasi", JOptionPane.INFORMATION_MESSAGE);
    return;
}

String isbnStr = fieldISBN.getText().trim();
if(isbnStr.isEmpty()) {
    JOptionPane.showMessageDialog(this, "ISBN tidak boleh kosong!", 
        "Peringatan", JOptionPane.WARNING_MESSAGE);
    return;
}

try {
    int isbn = Integer.parseInt(isbnStr);
    boolean ditemukan = false;
    
    for(Object obj : data_buku) {
        Object[] dataBuku = (Object[]) obj;
        int ISBN = (int) dataBuku[0];
        
        if(isbn == ISBN) {
            if(counterInputBuku == 0) {
                transaksiSementara[3] = ISBN;
            } else {
                transaksiSementara[4] = ISBN;
            }
            
            fieldJudul.setText((String) dataBuku[1]);
            fieldPenerbit.setText((String) dataBuku[2]);
            fieldTahunTerbit.setText((String) dataBuku[3]);
            updateCover((String) dataBuku[4]);
            
            counterInputBuku++;
            ditemukan = true;
            
            JOptionPane.showMessageDialog(this, 
                "Buku ke-" + counterInputBuku + " ditambahkan!\n" +
                "Sisa input: " + (jumlahBukuPinjam - counterInputBuku),
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
            
            if(counterInputBuku == jumlahBukuPinjam) {
                simpanTransaksi();
                updateBuku();
                
            }
            
            fieldISBN.setText("");
            
            break;
        }
    }
    
    if(!ditemukan) {
        JOptionPane.showMessageDialog(this, "Buku dengan ISBN " + isbn + " tidak ditemukan!", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
    
} catch(NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "ISBN harus angka!", 
        "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_pilihBukuButtonActionPerformed

    private void pinjamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pinjamButtonActionPerformed

    if(fieldNamaAnggota.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama anggota harus diisi!", 
            "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String kodeStr = fieldKodePinjam.getText().trim();
    if(kodeStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Kode Pinjam harus diisi!", 
            "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    try {
        int kodePinjam = Integer.parseInt(kodeStr);
        
        if(isKodePinjamExist(kodePinjam)) {
            JOptionPane.showMessageDialog(this, "Kode Pinjam sudah digunakan!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String jumlahStr = fieldJumlah.getText().trim();
        if(jumlahStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Jumlah buku harus diisi!", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int jumlahBuku = Integer.parseInt(jumlahStr);
        if(jumlahBuku < 1 || jumlahBuku > 2) {
            JOptionPane.showMessageDialog(this, "Jumlah buku minimal 1 dan maksimal 2!", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        jumlahBukuPinjam = jumlahBuku;
        statusMeminjam = true;
        counterInputBuku = 0;
        
        transaksiSementara = new Object[]{
            kodePinjam,
            new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
            fieldNamaAnggota.getText().trim(),
            0, 0, null, 0
        };
        
        fieldISBN.setText("");
        fieldJudul.setText("");
        fieldPenerbit.setText("");
        fieldTahunTerbit.setText("");
        updateCover("");
        fieldKodePinjam.setEnabled(false);
        
        JOptionPane.showMessageDialog(this, 
            "Masukkan " + jumlahBukuPinjam + " buku satu per satu melalui tombol 'Pilih Buku'",
            "Informasi", JOptionPane.INFORMATION_MESSAGE);
        
    } catch(NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Kode Pinjam dan Jumlah buku harus angka!", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }

    }//GEN-LAST:event_pinjamButtonActionPerformed

    private void kembalikanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kembalikanButtonActionPerformed
     
    String kodeStr = JOptionPane.showInputDialog(this, "Masukkan Kode Pinjam:");
if(kodeStr == null || kodeStr.trim().isEmpty()) return;

try {
    int kode = Integer.parseInt(kodeStr);
    boolean ditemukan = false;
    
    for(int i = 0; i < transaksi.length; i++) {
        Object[] dataTransaksi = (Object[]) transaksi[i];
        int kodeData = (int) dataTransaksi[0];
        
        if(kodeData == kode) {
            ditemukan = true;
            
            if(dataTransaksi[5] != null) {
                JOptionPane.showMessageDialog(this, "Buku sudah dikembalikan!", 
                    "Informasi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            if(dateKembalikan.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Pilih tanggal kembali!", 
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String tanggalKembali = new SimpleDateFormat("yyyy-MM-dd").format(dateKembalikan.getDate());
            dataTransaksi[5] = tanggalKembali;
            
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date tglPinjam = format.parse((String) dataTransaksi[1]);
                Date tglKembali = format.parse(tanggalKembali);
                
                long selisih = tglKembali.getTime() - tglPinjam.getTime();
                long selisihHari = selisih / (1000 * 60 * 60 * 24);
                
                int denda = 0;
                if(selisihHari > 5) {
                    denda = (int) ((selisihHari - 5) * 5000);
                    dataTransaksi[6] = denda;
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Pengembalian berhasil!\n" +
                    "Kode Pinjam: " + kodeData + "\n" +
                    "Nama Anggota: " + dataTransaksi[2] + "\n" +
                    "Tanggal Pinjam: " + dataTransaksi[1] + "\n" +
                    "Tanggal Kembali: " + tanggalKembali + "\n" +
                    "Total Hari: " + selisihHari + " hari\n" +
                    (denda > 0 ? "Denda: Rp " + denda : "Tidak ada denda"),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
            } catch(ParseException e) {
                e.printStackTrace();
            }
            
            loadTable();
            clearForm();
            break;
        }
    }
    
    if(!ditemukan) {
        JOptionPane.showMessageDialog(this, "Kode Pinjam tidak ditemukan!", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
    
} catch(NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "Kode Pinjam harus angka!", 
        "Error", JOptionPane.ERROR_MESSAGE);
}

    }//GEN-LAST:event_kembalikanButtonActionPerformed

    private void texitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_texitActionPerformed

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
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cekKodeButton;
    private com.toedter.calendar.JDateChooser dateKembalikan;
    private com.toedter.calendar.JDateChooser datePinjam;
    private javax.swing.JTextField fieldISBN;
    private javax.swing.JTextField fieldJudul;
    private javax.swing.JTextField fieldJumlah;
    private javax.swing.JTextField fieldKodePinjam;
    private javax.swing.JTextField fieldNamaAnggota;
    private javax.swing.JTextField fieldPenerbit;
    private javax.swing.JTextField fieldTahunTerbit;
    private javax.swing.JLabel iconCover;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton kembalikanButton;
    private javax.swing.JPanel panelPengembalian;
    private javax.swing.JPanel panelPilihBuku;
    private javax.swing.JPanel panelPinjam;
    private javax.swing.JButton pilihBukuButton;
    private javax.swing.JButton pinjamButton;
    private javax.swing.JButton texit;
    private javax.swing.JTable transaksiTable;
    // End of variables declaration//GEN-END:variables
}
