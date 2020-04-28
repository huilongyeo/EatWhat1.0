package com.example.eatwhat10;

import androidx.appcompat.app.AppCompatActivity;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.SharedPreferences;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.Window;
        import android.widget.ArrayAdapter;
        import android.widget.EditText;
        import android.widget.Button;
        import android.widget.PopupMenu;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.ImageView;
        import android.os.Bundle;
        import android.widget.AdapterView;
        import android.widget.Toast;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.content.Intent;

        import java.util.ArrayList;

        import com.example.eatwhat10.Database.FoodDBHelper;
        import com.example.eatwhat10.bean.Food;

/**
 * Created by huilongyeo on 25/4/20
 */
public class MainActivity extends AppCompatActivity implements OnClickListener{
    private Button btn_whatever;

    private FoodDBHelper mHelper;
    private SharedPreferences mShared;
    private int foodType;//choice of food(breakfast, lunch, dinner)用餐选择（早餐，午餐，晚餐）
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_whatever = findViewById(R.id.btn_whatever);
        btn_whatever.setOnClickListener(this);
        initSpinner();
        //get the object from share获得共享参数的对象
        mShared = getSharedPreferences("share", MODE_PRIVATE);
    }

    //initial Spinner 初始化下拉框
    private void initSpinner(){
        ArrayAdapter<String> foodAdapter = new ArrayAdapter<>(this, R.layout.item_select, foodArray);
        foodAdapter.setDropDownViewResource(R.layout.item_dropdown);
        Spinner sp_food = findViewById(R.id.sp_food);
        sp_food.setPrompt("请选择用餐类型");
        sp_food.setAdapter(foodAdapter);
        sp_food.setSelection(foodType);
        sp_food.setOnItemSelectedListener(new FoodSelectedListener());
    }

    private String[] foodArray = {"早餐吃......", "午餐吃......", "晚餐吃......"};
    class FoodSelectedListener implements AdapterView.OnItemSelectedListener{
        //set the food type设置用餐选择
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            foodType = arg2;
        }

        //do nothing未选择，无需关注
        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    protected void onResume(){
        super.onResume();
        mHelper = FoodDBHelper.getInstance(this, 1);
        mHelper.openReadLink();
        initFoodList();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mHelper.closeLink();
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.btn_whatever){//click the button 点击了按钮
            String food = null;
            String time = null;
            switch (foodType){
                case 0:
                    if(breakfastList.size() > 0){
                        time = "早餐";
                        int random = (int)(Math.random() * 10) % breakfastList.size();
                        food = breakfastList.get(random);
                    }else {
                        Toast.makeText(this, "没有早餐！！！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                case 1:
                    if(lunchList.size() > 0){
                        time = "午餐";
                        int random = (int)(Math.random() * 10) % lunchList.size();
                        food = lunchList.get(random);
                    }else {
                        Toast.makeText(this, "没有午餐！！！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
                case 2:
                    if(dinnerList.size() > 0){
                        time = "晚餐";
                        int random = (int)(Math.random() * 10) % dinnerList.size();
                        food = dinnerList.get(random);
                    }else {
                        Toast.makeText(this, "没有晚餐！！！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
            }
            final TextView tv_result = findViewById(R.id.tv_result);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("帮你决定");
            builder.setMessage(String.format("%s吃%s吗",time,food));
            builder.setPositiveButton("就吃这个", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Spinner sp = findViewById(R.id.sp_food);
                    sp.setVisibility(View.GONE);
                    btn_whatever.setVisibility(View.GONE);
                    tv_result.setVisibility(View.VISIBLE);
                }
            });
            builder.setNegativeButton("我再想想", null);
            AlertDialog alert = builder.create();
            alert.show();
            tv_result.setText(food);
        }
    }
    //create a function when open menu点击菜单选项时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //get menu layout获取菜单布局
        getMenuInflater().inflate(R.menu.menu_add_food, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();//get the id of menu item获取菜单项的编号
        SharedPreferences.Editor editor = mShared.edit();//get the object of editor获取编辑器对象
        if(id == R.id.menu_breakfast){//click the breakfast item点击“早餐”项
            editor.putInt("type", 1);//1 mean breakfast 1代表早餐
        }else if(id == R.id.menu_lunch){//click the lunch item点击“午餐”项
            editor.putInt("type", 2);//2 mean lunch 2代表午餐
        }else if(id == R.id.menu_dinner){//click the dinner item点击“晚餐”项
            editor.putInt("type", 3);//3 mean dinner 3代表晚餐
        }
        editor.commit();//submit the information to Shared提交资讯
        Intent intent = new Intent(this, AddFoodActivity.class);
        startActivity(intent);
        return true;
    }

    private ArrayList<String> breakfastList = new ArrayList<>();
    private ArrayList<String> lunchList = new ArrayList<>();
    private ArrayList<String> dinnerList = new ArrayList<>();

    //initial te list of food初始化食物列表
    public void initFoodList(){
        //clear and rest these FoodList清除并重设食物队列
        breakfastList.clear();
        lunchList.clear();
        dinnerList.clear();
        ArrayList<Food> infoArray = mHelper.query("1=1");
        for(int i = 0; i < infoArray.size(); i++){
            Food info = infoArray.get(i);
            int type = info.food_type;
            switch (type){
                case 1:
                    breakfastList.add(info.food_name);
                    break;
                case 2:
                    lunchList.add(info.food_name);
                    break;
                case 3:
                    dinnerList.add(info.food_name);
                    break;
            }
        }
    }

}
