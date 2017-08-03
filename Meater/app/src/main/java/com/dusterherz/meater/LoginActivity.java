package com.dusterherz.meater;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    public static final String EXTRA_USER_UID = "com.dusterherz.meater.USER_UID";
    private FirebaseAuth mAuth;
    private Button mBtnSend;
    private EditText mEdtEmail;
    private EditText mEdtPassword;
    private EditText mEdtPasswordConfirm;
    private TextView mTxtChangeLoginMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnSend.setFocusable(true);
        mBtnSend.setFocusableInTouchMode(true);
        mBtnSend.setOnClickListener(login);

        mEdtEmail = (EditText) findViewById(R.id.edt_email);
        mEdtPassword = (EditText) findViewById(R.id.edt_password);
        mEdtPasswordConfirm = (EditText) findViewById(R.id.edt_password_check);
        mTxtChangeLoginMode = (TextView) findViewById(R.id.txt_signin);
        mEdtEmail.setOnFocusChangeListener(setTextBlack);
        mEdtPassword.setOnFocusChangeListener(setTextBlack);
        mEdtPasswordConfirm.setOnFocusChangeListener(setTextBlack);
        mTxtChangeLoginMode.setOnClickListener(changeLoginMode);


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            openMainActivity(mAuth.getCurrentUser());
        }
    }

    private View.OnClickListener login = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mEdtEmail.clearFocus();
            mEdtPassword.clearFocus();
            mEdtPasswordConfirm.clearFocus();
            mBtnSend.requestFocus();
            if (!isEmailValid(mEdtEmail.getText().toString())) {
                mEdtEmail.setTextColor(getResources().getColor(R.color.error));
                Toast.makeText(LoginActivity.this,
                        getString(R.string.error_invalid_email),
                        Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (!isPasswordValid(mEdtPassword.getText().toString())) {
                mEdtPassword.setTextColor(getResources().getColor(R.color.error));
                Toast.makeText(LoginActivity.this,
                        getString(R.string.error_invalid_password),
                        Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (mEdtPasswordConfirm.getVisibility() == View.VISIBLE &&
                    !isPasswordCheckValid(mEdtPassword.getText().toString(),
                    mEdtPasswordConfirm.getText().toString())) {
                mEdtPasswordConfirm.setTextColor(getResources().getColor(R.color.error));
                Toast.makeText(LoginActivity.this,
                        getString(R.string.error_invalid_check_password),
                        Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (mEdtPasswordConfirm.getVisibility() == View.VISIBLE) {
                createNewUser(mEdtEmail.getText().toString(), mEdtPassword.getText().toString());
            } else {
                signInUser(mEdtEmail.getText().toString(), mEdtPassword.getText().toString());
            }
        }
    };

    private View.OnFocusChangeListener setTextBlack = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                ((EditText) v).setTextColor(getResources().getColor(R.color.black));
            }
        }
    };

    private View.OnClickListener changeLoginMode = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            TextView txtPasswordConfirm = (TextView) findViewById(R.id.txt_password_check);
            if (mEdtPasswordConfirm.getVisibility() == View.VISIBLE) {
                mEdtPasswordConfirm.setVisibility(View.GONE);
                txtPasswordConfirm.setVisibility(View.GONE);
                mTxtChangeLoginMode.setText(R.string.txt_signin);
            } else {
                mEdtPasswordConfirm.setVisibility(View.VISIBLE);
                txtPasswordConfirm.setVisibility(View.VISIBLE);
                mTxtChangeLoginMode.setText(R.string.txt_login);
            }
        }
    };

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private boolean isPasswordCheckValid(String password1, String password2){
        return password1.equals(password2);
    }

    private void createNewUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            openMainActivity(user);
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.error_register,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            openMainActivity(user);
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.error_authentification,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openMainActivity(FirebaseUser user) {
        String uid = user.getUid();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_USER_UID, uid);
        startActivity(intent);
    }
}
