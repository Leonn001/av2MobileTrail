package com.example.apptrail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button regbtn;
    private Button managebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        regbtn = findViewById(R.id.registerbtn);
        managebtn = findViewById(R.id.managebtn);
        onClick(regbtn);
        onClick(managebtn);
    }

    public void onClick (Button btn) {
        if (btn == regbtn) {
            btn.setOnClickListener(v->{
                Intent intent = new Intent(this, RegisterTrailActivity.class);
                startActivity(intent);
            });
        }

        if (btn == managebtn) {
            btn.setOnClickListener(v->{
                Intent intent = new Intent(this, ManageTrailActivity.class);
                startActivity(intent);
            });
        }
    }
}