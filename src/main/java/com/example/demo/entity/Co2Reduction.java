package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "co2_reduction")
public class Co2Reduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private double baseline;
    private double actual;
    private double reduction;
    private boolean certified;
    private String status;

    public Co2Reduction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public double getBaseline() { return baseline; }
    public void setBaseline(double baseline) { this.baseline = baseline; }
    public double getActual() { return actual; }
    public void setActual(double actual) { this.actual = actual; }
    public double getReduction() { return reduction; }
    public void setReduction(double reduction) { this.reduction = reduction; }
    public boolean isCertified() { return certified; }
    public void setCertified(boolean certified) { this.certified = certified; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
