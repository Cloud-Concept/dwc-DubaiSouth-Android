package model;

/**
 * Created by Abanoub Wagdy on 10/1/2015.
 */
public class EstablishmentCard {

    private String Current_Establishment_Card__c;
    String Status;
    String Establishment_Card_Number__c;
    String Issue_Date__c;
    String Expiry_Date__c;

    public String getCurrent_Establishment_Card__c() {
        return Current_Establishment_Card__c;
    }

    public void setCurrent_Establishment_Card__c(String current_Establishment_Card__c) {
        Current_Establishment_Card__c = current_Establishment_Card__c;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getEstablishment_Card_Number__c() {
        return Establishment_Card_Number__c;
    }

    public void setEstablishment_Card_Number__c(String stablishment_Card_Number__c) {
        this.Establishment_Card_Number__c = stablishment_Card_Number__c;
    }

    public String getIssue_Date__c() {
        return Issue_Date__c;
    }

    public void setIssue_Date__c(String issue_Date__c) {
        Issue_Date__c = issue_Date__c;
    }

    public String getExpiry_Date__c() {
        return Expiry_Date__c;
    }

    public void setExpiry_Date__c(String expiry_Date__c) {
        Expiry_Date__c = expiry_Date__c;
    }
}
