package com.example.eatwhat10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by huilongyeo on 26/4/2020
 */
public class FoodEditActivity extends AppCompatActivity implements OnClickListener{

    private EditText et_edit;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_edit);
        et_edit= findViewById(R.id.et_edit);
        findViewById(R.id.btn_edit).setOnClickListener(this);
        showHint();
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.btn_edit){
            String name = et_edit.getText().toString();
            if(TextUtils.isEmpty(name)){
                Toast.makeText(this, "请输入食物名称",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("food_name", name);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void showHint(){
       bundle = getIntent().getExtras();
       String name = bundle.getString("food_name");
       et_edit.setText(name);
    }
}
