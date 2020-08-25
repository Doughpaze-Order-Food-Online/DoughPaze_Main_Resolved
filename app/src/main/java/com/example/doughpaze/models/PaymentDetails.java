package com.example.doughpaze.models;

public class PaymentDetails {
    private String status,bankname, date, transactionId, paymentType, bankTransactionId;
    private Double amountpaid;
    String orderId;
    private Boolean payment_status;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public void setBankTransactionId(String bankTransactionId) {
        this.bankTransactionId = bankTransactionId;
    }


    public void setAmountpaid(Double amountpaid) {
        this.amountpaid = amountpaid;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public Double getAmountpaid() {
        return amountpaid;
    }

    public String getBankTransactionId() {
        return bankTransactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setPayment_status(Boolean payment_status) {
        this.payment_status = payment_status;
    }

    public Boolean getPayment_status() {
        return payment_status;
    }
}

