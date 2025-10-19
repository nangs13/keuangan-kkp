package com.kkp.keuangan.backend.model;

public class ModelCoa {
    private int id;
    private String code;
    private String nama;
    private String periode;   // format yyyymm
    private String type;      // debit / credit
    private Integer parentId;
    private double beginning;
    private double debit;
    private double credit;
    private double ending;

    public ModelCoa() {
        // Constructor kosong
    }

    public ModelCoa(int id, String code, String nama, String periode, String type,
                    Integer parentId, double beginning, double debit, double credit, double ending) {
        this.id = id;
        this.code = code;
        this.nama = nama;
        this.periode = periode;
        this.type = type;
        this.parentId = parentId;
        this.beginning = beginning;
        this.debit = debit;
        this.credit = credit;
        this.ending = ending;
    }

    public ModelCoa(String code, String nama, String periode, String type,
                    Integer parentId, double beginning, double debit, double credit, double ending) {
        this(0, code, nama, periode, type, parentId, beginning, debit, credit, ending);
    }

    // Getter
    public int getId() { return id; }
    public String getCode() { return code; }
    public String getNama() { return nama; }
    public String getPeriode() { return periode; }
    public String getType() { return type; }
    public Integer getParentId() { return parentId; }
    public double getBeginning() { return beginning; }
    public double getDebit() { return debit; }
    public double getCredit() { return credit; }
    public double getEnding() { return ending; }

    // Setter
    public void setId(int id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setNama(String nama) { this.nama = nama; }
    public void setPeriode(String periode) { this.periode = periode; }
    public void setType(String type) { this.type = type; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public void setBeginning(double beginning) { this.beginning = beginning; }
    public void setDebit(double debit) { this.debit = debit; }
    public void setCredit(double credit) { this.credit = credit; }
    public void setEnding(double ending) { this.ending = ending; }
    
    @Override
    public String toString() {
        return code + " - " + nama; // tampilkan kode + nama
    }
}
