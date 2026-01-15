package com.joelcode.pokerchipsapplication.dto.other;

public class PlayerChipStatistics {
    private Long totalSent;
    private Long totalReceived;
    private Long netGain;
    private Integer transactionCount;

    public PlayerChipStatistics() {}

    public PlayerChipStatistics(Long totalSent, Long totalReceived, Long netGain, Integer transactionCount) {
        this.totalSent = totalSent;
        this.totalReceived = totalReceived;
        this.netGain = netGain;
        this.transactionCount = transactionCount;
    }

    public Long getTotalSent() {return totalSent;}
    public void setTotalSent(Long totalSent) {this.totalSent = totalSent;}

    public Long getTotalReceived() {return totalReceived;}
    public void setTotalReceived(Long totalReceived) {this.totalReceived = totalReceived;}

    public Long getNetGain() {return netGain;}
    public void setNetGain(Long netGain) {this.netGain = netGain;}

    public Integer getTransactionCount() {return transactionCount;}
    public void setTransactionCount(Integer transactionCount) {this.transactionCount = transactionCount;}
}
