package com.kkp.keuangan.laporan;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

// ----- MODEL DATA (Simulasi database) -----
class KasRecord {
    String namaAkun;
    String saldoAwal;
    String penerimaan;
    String pengeluaran;
    String mutasi;
    String saldoAkhir;

    public KasRecord(String namaAkun, String saldoAwal, String penerimaan,
                    String pengeluaran, String mutasi, String saldoAkhir) {
        this.namaAkun = namaAkun;
        this.saldoAwal = saldoAwal;
        this.penerimaan = penerimaan;
        this.pengeluaran = pengeluaran;
        this.mutasi = mutasi;
        this.saldoAkhir = saldoAkhir;
    }
}
public class LaporanKas extends JPanel {
    JTable table;
    DefaultTableModel model;

    public LaporanKas() {

        setLayout(new BorderLayout());

        // Header tabel
        String[] kolom = {
            "Nama Akun",
            "Saldo Awal",
            "Penerimaan",
            "Pengeluaran",
            "Mutasi",
            "Saldo Akhir"
        };

        model = new DefaultTableModel(kolom, 0);
        table = new JTable(model);

        // Tambahkan data dari "database"
        List<KasRecord> dataKas = ambilDataDariDatabase();
        masukkanDataKeTabel(dataKas);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    // Simulasi mengambil data dari database
    public List<KasRecord> ambilDataDariDatabase() {
        List<KasRecord> list = new ArrayList<>();

        list.add(new KasRecord(
            "Kas setara kas", "Rp. 200,000,000", "Rp. 0", "Rp. 0", "Rp. 0", "Rp. 200,000,000"
        ));
        list.add(new KasRecord(
            "Kas operasional", "Rp. 200,000,000", "Rp. 0", "Rp. 2,224,000", "Rp. -2,224,000", "Rp. 197,776,000"
        ));
        list.add(new KasRecord(
            "Kas kecil", "Rp. 197,776,000", "Rp. 20,000", "Rp. 0", "Rp. 20,000", "Rp. 197,796,000"
        ));
        list.add(new KasRecord(
            "Bank", "Rp. 197,796,000", "Rp. 0", "Rp. 0", "Rp. 0", "Rp. 197,796,000"
        ));

        return list;
    }


    // Memasukkan data ke dalam tabel
    private void masukkanDataKeTabel(List<KasRecord> list) {
        for (KasRecord r : list) {
            model.addRow(new Object[]{
                r.namaAkun,
                r.saldoAwal,
                r.penerimaan,
                r.pengeluaran,
                r.mutasi,
                r.saldoAkhir
            });
        }
    }

    // MAIN untuk test panel
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new javax.swing.JFrame() {{
                setContentPane(new LaporanKas());
                setTitle("Laporan Kas");
                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                pack();
                setVisible(true);
            }};
        });
    }
}