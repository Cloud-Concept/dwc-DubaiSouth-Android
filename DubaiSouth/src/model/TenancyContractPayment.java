package model;

import com.google.gson.annotations.SerializedName;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Abanoub Wagdy on 10/11/2015.
 */
public class TenancyContractPayment {

    @JsonProperty("Id")
    @SerializedName("Id")
    String Id;
    @JsonProperty("Name")
    @SerializedName("Name")
    String name;
    @JsonProperty("Description__c")
    @SerializedName("Description__c")
    String descriptionPayment;
    @JsonProperty("Payment_Amount__c")
    @SerializedName("Payment_Amount__c")
    String paymentAmount;
    @JsonProperty("Due_Date__c")
    @SerializedName("Due_Date__c")
    String dueDate;
    @JsonProperty("Due_Date_To__c")
    @SerializedName("Due_Date_To__c")
    String dueDateTo;
    @SerializedName("Inventory_Unit__c")
    @JsonProperty("Inventory_Unit__c")
    String Inventory_Unit__c;
    @SerializedName("Tenancy_Contract__c")
    @JsonProperty("Tenancy_Contract__c")
    String Tenancy_Contract__c;

    public String getInventory_Unit__c() {
        return Inventory_Unit__c;
    }

    public void setInventory_Unit__c(String inventory_Unit__c) {
        Inventory_Unit__c = inventory_Unit__c;
    }

    public String getTenancy_Contract__c() {
        return Tenancy_Contract__c;
    }

    public void setTenancy_Contract__c(String tenancy_Contract__c) {
        Tenancy_Contract__c = tenancy_Contract__c;
    }

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
