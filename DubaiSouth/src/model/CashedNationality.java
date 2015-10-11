package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by M_Ghareeb on 10/11/2015.
 */
public class CashedNationality implements Serializable {
    public ArrayList<Country__c> getCountries() {
        return countries;
    }

    public CashedNationality setCountries(ArrayList<Country__c> countries) {
        this.countries = countries;
        return this;
    }

    ArrayList<Country__c> countries;
}
