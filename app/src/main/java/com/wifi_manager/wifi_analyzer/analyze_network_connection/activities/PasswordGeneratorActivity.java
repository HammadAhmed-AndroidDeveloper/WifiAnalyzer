package com.wifi_manager.wifi_analyzer.analyze_network_connection.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.wifi_manager.wifi_analyzer.analyze_network_connection.R;

import java.util.Random;

public class PasswordGeneratorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-=+{}[]<>,.?";
    private static final String ALPHAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lower = "abcdefghijklmnopqrstuvwxyz";
    private static final String lowerChars = "abcdefghijklmnopqrstuvwxyz!@#$%^&*()-=+{}[]<>,.?";
    private static final String lowerNumeric = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final String lowerUpper = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String upperLower = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String upperNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-=+{}[]<>,.?";
    private static final String AlphasNums = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final String AlphasNumsChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-=+{}[]<>,.?";
    private static final String UppLowerChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-=+{}[]<>,.?";
    private static final String UpperNumericChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-=+{}[]<>,.?";
    private static final String lowerNumericChars = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-=+{}[]<>,.?";
    private static final String AlphaNums = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String numbers = "0123456789";
    private static final String chars = "!@#$%^&*()-=+{}[]<>,.?";

    SwitchCompat upperCase, lowerCase, numeric, symbols;

    EditText passwordEditTExt, passwordLength;
    Button generatePassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generator);

        generatePassword = findViewById(R.id.generatePassword);
        passwordEditTExt = findViewById(R.id.passwordEditTExt);
        upperCase = findViewById(R.id.upperCasePassword);
        lowerCase = findViewById(R.id.lowerCasePassword);
        numeric = findViewById(R.id.numberCasePassword);
        symbols = findViewById(R.id.symbolCasePassword);
        passwordLength = findViewById(R.id.passwordLength);

        generatePassword.setOnClickListener(this);
    }

    private String getPassword(String which) {
        StringBuilder password = new StringBuilder();
        String s = passwordLength.getText().toString();
        int n = 8;
        if (s != null && s.length() > 0) {
            n = Integer.parseInt(passwordLength.getText().toString());
        }

        for (int i = 0; i < n; i++) {
            password.append(which.charAt(new Random().nextInt(which.length())));
        }
        return password.toString();
    }

    public String generatePassword(String which) {
        switch (which) {
            case "onlyAplhas" -> {
                return getPassword(ALPHAS);
            }
            case "numsAlphas" -> {
                return getPassword(AlphasNums);
            }
            case "nums" -> {
                return getPassword(numbers);
            }
            case "numsAlphasChars" -> {
                return getPassword(AlphasNumsChars);
            }
            case "chars" -> {
                return getPassword(chars);
            }
            case "lower" -> {
                return getPassword(lower);
            }
            case "AlphaNums" -> {
                return getPassword(AlphaNums);
            }
            case "UppLowerChars" -> {
                return getPassword(UppLowerChars);
            }
            case "UpperNumericChars" -> {
                return getPassword(UpperNumericChars);
            }
            case "lowerNumericChars" -> {
                return getPassword(lowerNumericChars);
            }
            case "upperLower" -> {
                return getPassword(upperLower);
            }
            case "upperNumeric" -> {
                return getPassword(upperNumeric);
            }
            case "upperChars" -> {
                return getPassword(upperChars);
            }
            case "lowerChars" -> {
                return getPassword(lowerChars);
            }
            case "lowerNumeric" -> {
                return getPassword(lowerNumeric);
            }
            case "lowerUpper" -> {
                return getPassword(lowerUpper);
            }
            default -> {
                return getPassword(CHARS);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.generatePassword) {
            if (upperCase.isChecked() && lowerCase.isChecked() && numeric.isChecked() && symbols.isChecked()) {
                passwordEditTExt.setText(generatePassword("CHARS"));
            } else if (upperCase.isChecked() && lowerCase.isChecked() && numeric.isChecked()) {
                passwordEditTExt.setText(generatePassword("AlphaNums"));
            } else if (upperCase.isChecked() && lowerCase.isChecked() && symbols.isChecked()) {
                passwordEditTExt.setText(generatePassword("UppLowerChars"));
            } else if (upperCase.isChecked() && numeric.isChecked() && symbols.isChecked()) {
                passwordEditTExt.setText(generatePassword("UpperNumericChars"));
            } else if (lowerCase.isChecked() && numeric.isChecked() && symbols.isChecked()) {
                passwordEditTExt.setText(generatePassword("lowerNumericChars"));
            } else if (upperCase.isChecked() && lowerCase.isChecked()) {
                passwordEditTExt.setText(generatePassword("upperLower"));
            } else if (upperCase.isChecked() && numeric.isChecked()) {
                passwordEditTExt.setText(generatePassword("upperNumeric"));
            } else if (upperCase.isChecked() && symbols.isChecked()) {
                passwordEditTExt.setText(generatePassword("upperChars"));
            } else if (lowerCase.isChecked() && upperCase.isChecked()) {
                passwordEditTExt.setText(generatePassword("lowerUpper"));
            } else if (lowerCase.isChecked() && numeric.isChecked()) {
                passwordEditTExt.setText(generatePassword("lowerNumeric"));
            } else if (lowerCase.isChecked() && symbols.isChecked()) {
                passwordEditTExt.setText(generatePassword("lowerChars"));
            } else if (upperCase.isChecked()) {
                passwordEditTExt.setText(generatePassword("onlyAplhas"));
            } else if (lowerCase.isChecked()) {
                passwordEditTExt.setText(generatePassword("lower"));
            } else if (numeric.isChecked()) {
                passwordEditTExt.setText(generatePassword("nums"));
            } else if (symbols.isChecked()) {
                passwordEditTExt.setText(generatePassword("nums"));
            }
        }
    }
}