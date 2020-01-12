package com.example.networkingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText email, pass;
    TextView noAccount, recoverPass;
    Button loginBtn;

    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        noAccount = findViewById(R.id.notHave_account);
        recoverPass = findViewById(R.id.recoverPassTv);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailS = email.getText().toString().trim();
                String passS = pass.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(emailS).matches()) {
                    email.setError("Invalid Email");
                    email.setFocusable(true);
                } else {
                    loginUser(emailS, passS);
                }

            }
        });

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        recoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPassword();
            }

        });

        progressDialog = new ProgressDialog(this);
    }

    private void showRecoverPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout linearLayout = new LinearLayout(this);

        final EditText email = new EditText(this);
        email.setHint("Email");
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        email.setMinEms(16);

        linearLayout.addView(email);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);

        //button recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //put email
                String emailS = email.getText().toString().trim();
                beginRecovery(emailS);

            }
        });
        //button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //close dialog
                dialog.dismiss();
            }
        });

        //show dialog
        builder.create().show();

    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //show error msg
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String emailS, String passS) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(emailS, passS)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();

                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
