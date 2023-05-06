package modiriate.hoshmand.abyari;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.app.AlertDialog;
import android.content.Context;

public class ActivityLogin extends AppCompatActivity {

    Button btnLogin,btnAbout;
    EditText edName,edPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.bulogin);
        btnAbout = findViewById(R.id.buabout);
        edName = findViewById(R.id.editText1);
        edPass = findViewById(R.id.editText3);

        btnLogin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String name = edName.getText().toString();
                    String pass = edPass.getText().toString();
                    if (name.equals("Admin") && pass.equals("admin")) {
                        finish();
                        startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                    } else {
                        Toast.makeText(ActivityLogin.this, "نام کاربری یا کلمه عبور اشتباه است", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        btnAbout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View myScrollView = inflater.inflate(R.layout.activity_about, null, false);
                    new AlertDialog.Builder(ActivityLogin.this).setView(myScrollView).setTitle("درباره ما").setPositiveButton("تایید", null).show();
                }
            });

    }

}
