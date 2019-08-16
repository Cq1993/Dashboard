package cq.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final DashBoardView dbv = findViewById(R.id.dbv);
        dbv.setBtnClickListener(new DashBoardView.OnBtnClick() {
            @Override
            public void onClick() {
                dbv.setData(1500);
            }
        });
    }
}
