package com.mc2022.template;

import androidx.annotation.NonNull;
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
    private int symptomId = 0;
    private CovidPatient user;

    private EditText editTextName;
    private TextView textViewSymptom;

    private Button buttonYes;
    private Button buttonNo;

    private Button buttonNext;

    private Button buttonSubmit;
    private Button buttonClear;

    private String[] symptoms = new String[]{
            "fever",
            "runny nose",
            "scratchy throat",
            "body ache",
            "head ache",
            "loss of taste"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = (EditText) findViewById(R.id.editTextName);
        textViewSymptom = (TextView) findViewById(R.id.textViewSymptom);
        buttonYes = (Button) findViewById(R.id.buttonYes);
        buttonNo = (Button) findViewById(R.id.buttonNo);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonClear = (Button) findViewById(R.id.buttonClear);

        if (savedInstanceState!=null) {
            user = (CovidPatient) savedInstanceState.getSerializable("user");
            symptomId = savedInstanceState.getInt("symptomId");
        }
        else {
            user = new CovidPatient();
            symptomId = 0;
        }

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

        textViewSymptom.setText(symptoms[symptomId]);

        buttonYes.setOnClickListener(view -> {
            user.setSymptom((String) textViewSymptom.getText(), true);
            Log.i(TAG, "Yes:"+textViewSymptom.getText().toString());
        });

        buttonNo.setOnClickListener(view -> {
            user.setSymptom((String) textViewSymptom.getText(), false);
            Log.i(TAG, "No:"+textViewSymptom.getText().toString());
        });

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

        buttonClear.setOnClickListener(view -> {
            user = new CovidPatient();
            symptomId = 0;

            textViewSymptom.setText(symptoms[symptomId]);
            editTextName.clearComposingText();
            editTextName.setText("");

            Log.i(TAG, "clear: the form was reset");
            Toast.makeText(MainActivity.this, "clear: the form was reset", Toast.LENGTH_SHORT).show();
        });

        Log.i(TAG, "state: onCreate");
        Toast.makeText(MainActivity.this, TAG + ", state: onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "state: onStart");
        Toast.makeText(MainActivity.this, TAG + ", state: onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "state: onResume");
        Toast.makeText(MainActivity.this, TAG + ", state: onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "state: onPause");
        Toast.makeText(MainActivity.this, TAG + ", state: onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "state: onStop");
        Toast.makeText(MainActivity.this, TAG + ", state: onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "state: onDestroy");
        Toast.makeText(MainActivity.this, TAG + ", state: onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "state: onRestart");
        Toast.makeText(MainActivity.this, TAG + ", state: onRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("user", user);
        savedInstanceState.putInt("symptomId", symptomId);
        Log.i(TAG, "state: onSaveInstanceState");
        Toast.makeText(MainActivity.this, TAG + ", state: onSaveInstanceState", Toast.LENGTH_SHORT).show();
    }
}