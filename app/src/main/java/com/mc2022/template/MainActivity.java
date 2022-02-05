package com.mc2022.template;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FirstActivity";

    // state variables
    private int symptomId = 0;

    // Model instance
    private CovidUser user;

    // Widgets
    private EditText editTextName;
    private TextView textViewSymptom;

    private Button buttonYes;
    private Button buttonNo;

    private Button buttonNext;

    private Button buttonSubmit;
    private Button buttonClear;

    // other helper variables
    private String[] symptoms;
    private String previousState = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        symptoms = new String[]{
            getString(R.string.symptom1),
            getString(R.string.symptom2),
            getString(R.string.symptom3),
            getString(R.string.symptom4),
            getString(R.string.symptom5),
            getString(R.string.symptom6),
        };

        editTextName = (EditText) findViewById(R.id.editTextName);
        textViewSymptom = (TextView) findViewById(R.id.textViewSymptom);
        buttonYes = (Button) findViewById(R.id.buttonYes);
        buttonNo = (Button) findViewById(R.id.buttonNo);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonClear = (Button) findViewById(R.id.buttonClear);

        // handling activity restart - restoring the state
        if (savedInstanceState!=null) {
            user = (CovidUser) savedInstanceState.getSerializable("user");
            symptomId = savedInstanceState.getInt("symptomId");
        }
        else {
            user = new CovidUser();
            symptomId = 0;
        }

        /* CONTROLLER logic */

        // input name
        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                user.setName(charSequence.toString());
                Log.i(TAG, "onTextChanged, name=" + user.getName());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // display symptom
        textViewSymptom.setText(symptoms[symptomId]);

        // yes button
        buttonYes.setOnClickListener(view -> {
            user.setSymptom((String) textViewSymptom.getText(), true);
            Log.i(TAG, "Yes:"+textViewSymptom.getText().toString());
        });

        // no button
        buttonNo.setOnClickListener(view -> {
            user.setSymptom((String) textViewSymptom.getText(), false);
            Log.i(TAG, "No:"+textViewSymptom.getText().toString());
        });

        // next symptom, only proceeds if answered current one
        buttonNext.setOnClickListener(view -> {
            if (user!=null && user.getSymptoms().containsKey(textViewSymptom.getText())) {
                if (symptomId < symptoms.length - 1) {
                    symptomId++;
                    textViewSymptom.setText(symptoms[symptomId]);
                } else {
                    Toast.makeText(MainActivity.this, "next: end of symptoms list", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Log.i(TAG, "next: pls enter yes or no");
                Toast.makeText(MainActivity.this, "next: pls enter yes or no", Toast.LENGTH_SHORT).show();
            }
        });

        // submit info and move to second_activity
        buttonSubmit.setOnClickListener(view -> {
            if (user!=null && user.getName()!=null && user.getSymptomsLength()==symptoms.length) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
            else{
                Log.i(TAG, "submit: missing info");
                Toast.makeText(MainActivity.this, "submit: missing info", Toast.LENGTH_SHORT).show();
            }
        });

        // clear or reset the form
        buttonClear.setOnClickListener(view -> {
            user = new CovidUser();
            symptomId = 0;

            textViewSymptom.setText(symptoms[symptomId]);
            editTextName.clearComposingText();
            editTextName.setText("");

            Log.i(TAG, "clear: the form was reset");
            Toast.makeText(MainActivity.this, "clear: the form was reset", Toast.LENGTH_SHORT).show();
        });

        stateLogAndToast("Created");
    }

    private void stateLogAndToast(String currentState)
    {
        String msg = "State of " + TAG + " changed";
        if (previousState.equals("")) {
            msg += " to " + currentState;
        }
        else {
            msg += " from " + previousState + " to " + currentState;
        }
        previousState = currentState;

        Log.i(TAG, msg);
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        stateLogAndToast("Started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        stateLogAndToast("Resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stateLogAndToast("Paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        stateLogAndToast("Stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stateLogAndToast("Destroyed");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        stateLogAndToast("Restarted");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // saving state variables
        savedInstanceState.putSerializable("user", user);
        savedInstanceState.putInt("symptomId", symptomId);
        Log.i(TAG, "onSaveInstanceState");
        Toast.makeText(MainActivity.this, TAG + ", onSaveInstanceState", Toast.LENGTH_SHORT).show();
    }
}