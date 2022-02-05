package com.mc2022.template;

import java.io.Serializable;
import java.util.HashMap;

/*
* MODEL CLASS
*/

public class CovidUser implements Serializable {

    // store the user's name
    private String name;

    // store the user's responses to each symptom (true - yes, false - no)
    private HashMap<String,Boolean> symptoms = new HashMap<String, Boolean>();

    /* Getters and Setters */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Boolean> getSymptoms() {
        return symptoms;
    }

    public void setSymptom(String symptom, boolean value) {
        this.symptoms.put(symptom, value);
    }

    /* Helper Methods */
    public int getSymptomsLength() {
        return symptoms.size();
    }

    public boolean isTestNeeded() {
        int ctr = 0;
        for( boolean v : symptoms.values())
        {
            if (v)
            {
                ctr++;
            }
        }

        return ctr>3;
    }

    @Override
    public String toString() {
        String str = "Name: " + name + '\n';
        for (String key : symptoms.keySet())
        {
            String val = symptoms.get (key)?"Yes":"No";

            str += key + ":" + val + "\n";
        }
        return str;
    }
}
