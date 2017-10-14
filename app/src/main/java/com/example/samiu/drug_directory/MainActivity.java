package com.example.samiu.drug_directory;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class MainActivity extends Activity implements View.OnClickListener{

    Button vf_01_okayButton,yesButton,noButton,vf_03_okayButton,vf_04_okayButton,vf_05_doneButton,vf_06_donebutton;
    ViewFlipper vf;
    EditText vf_05_emailText,vf_06_passText;
    TextView vf_06_givenEmail;
    StickySwitch vf_07_stickySwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        vf_01_okayButton = findViewById(R.id.button);
        vf_01_okayButton.setOnClickListener(this);
        vf = findViewById(R.id.flipper01);
        yesButton = findViewById(R.id.yesButtonId);
        yesButton.setOnClickListener(this);
        noButton = findViewById(R.id.noButtonId);
        noButton.setOnClickListener(this);
        vf_03_okayButton = findViewById(R.id.greetingsOkaybuttonId);
        vf_03_okayButton.setOnClickListener(this);
        vf_04_okayButton = findViewById(R.id.vf_04_OkaybuttonId);
        vf_04_okayButton.setOnClickListener(this);
        vf_05_emailText = findViewById(R.id.emailText);
        vf_05_emailText.setPadding(45,0,0,0);
        vf_06_passText = findViewById(R.id.passTextId);
        vf_06_passText.setPadding(45,0,0,0);
        vf_05_doneButton = findViewById(R.id.vf_05_donebuttonId);
        vf_05_doneButton.setOnClickListener(this);
        vf_06_givenEmail = findViewById(R.id.givenEmailId);
        vf_06_donebutton = findViewById(R.id.vf_06_donebuttonId);
        vf_06_donebutton.setOnClickListener(this);
        vf_07_stickySwitch = findViewById(R.id.stick_switch);
        vf_07_stickySwitch.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction,@NotNull String s){
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View view) {
        if(view == vf_01_okayButton){
            vf.showNext();
        }
        else if(view == yesButton){

        }
        else if(view == noButton){
            vf.showNext();
        }
        else if(view == vf_03_okayButton){
            vf.showNext();
        }
        else if (view == vf_04_okayButton){
            vf.showNext();
        }
        else if(view == vf_05_doneButton){
            vf_06_givenEmail.setText(vf_05_emailText.getText());
            vf.showNext();
        }
        else if(view == vf_06_donebutton){
            vf.showNext();
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
