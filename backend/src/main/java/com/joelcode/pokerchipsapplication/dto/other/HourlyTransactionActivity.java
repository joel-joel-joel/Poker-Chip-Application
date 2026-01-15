package com.joelcode.pokerchipsapplication.dto.other;

public class HourlyTransactionActivity {

    private Integer hour;
    private Long transactionCount;

    public HourlyTransactionActivity() {}

    public HourlyTransactionActivity(Integer hour, Long transactionCount) {
        this.hour = hour;
        this.transactionCount = transactionCount;
    }

    public Integer getHour() {return hour;}
    public void setHour(Integer hour) {this.hour = hour;}

    public Long getTransactionCount() {return transactionCount;}
    public void setTransactionCount(Long transactionCount) {this.transactionCount = transactionCount;}

}
