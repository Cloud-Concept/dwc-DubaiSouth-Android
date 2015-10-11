package model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Abanoub Wagdy on 10/11/2015.
 */
public class TenancyContractPayment {

    @JsonProperty("Id")
    String Id;
    @JsonProperty("Name")
    String name;
    @JsonProperty("Description__c")
    String descriptionPayment;
    @JsonProperty("Payment_Amount__c")
    String paymentAmount;
    @JsonProperty("Due_Date__c")
    String dueDate;
    @JsonProperty("Due_Date_To__c")
    String dueDateTo;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptionPayment() {
        return descriptionPayment;
    }

    public void setDescriptionPayment(String descriptionPayment) {
        this.descriptionPayment = descriptionPayment;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDateTo() {
        return dueDateTo;
    }

    public void setDueDateTo(String dueDateTo) {
        this.dueDateTo = dueDateTo;
    }
}
