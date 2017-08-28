package com.example.cg.custompwd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cg.custompwd.custom.simpleCustomPwd;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private simpleCustomPwd myPwd;
    private Button btn_getNumber;
    private Button btn_setNumber;
    private Button btn_CancelNumber;
    private TextView txt_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initControls();
    }

    /**
     * 初始化控件
     */
    private void initControls() {
        myPwd = (simpleCustomPwd)findViewById(R.id.myPwd);
        myPwd.setOnInputEnterLitener(new simpleCustomPwd.onInputEnterLitener() {
            @Override
            public void onInputEnter(String password) {
                txt_info.setText(myPwd.getNumber());
            }
        });


        btn_getNumber = (Button)findViewById(R.id.btn_getNumber);
        btn_getNumber.setOnClickListener(this);
        btn_setNumber = (Button)findViewById(R.id.btn_setNumber);
        btn_setNumber.setOnClickListener(this);
        btn_CancelNumber = (Button)findViewById(R.id.btn_CancelNumber);
        btn_CancelNumber.setOnClickListener(this);

        txt_info = (TextView)findViewById(R.id.txt_info);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_getNumber:
                txt_info.setText(myPwd.getNumber());
                break;
            case R.id.btn_setNumber:
                myPwd.setNumber("986512");
                break;
            case R.id.btn_CancelNumber:
                myPwd.cancelNumber();
                break;
        }
    }
}
