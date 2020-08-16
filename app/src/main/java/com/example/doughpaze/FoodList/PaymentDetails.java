package com.example.doughpaze.FoodList;

public class PaymentDetails {
    private String status,bankname, date, transactionId, paymentType, bankTransactionId;
    private Double amountpaid,orderId;

    public void setOrderId(Double orderId) {
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

    public Double getOrderId() {
        return orderId;
    }

    public Double getAmountpaid() {
        return amountpaid;
    }
}

