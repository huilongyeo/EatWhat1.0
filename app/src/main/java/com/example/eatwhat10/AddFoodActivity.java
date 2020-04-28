package com.example.eatwhat10;

        import androidx.annotation.DrawableRes;
        import androidx.appcompat.app.AppCompatActivity;

        import android.annotation.SuppressLint;
        import android.app.AlertDialog;
        import android.content.ContentValues;
        import android.content.Intent;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.content.SharedPreferences;
        import android.text.Layout;
        import android.text.TextUtils;
        import android.util.TypedValue;
        import android.view.ContextMenu;
        import android.view.Gravity;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.EditText;
        import android.widget.Toast;
        import android.widget.LinearLayout.LayoutParams;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

        import com.example.eatwhat10.bean.Food;
        import com.example.eatwhat10.Database.FoodDBHelper;



public class AddFoodActivity extends AppCompatActivity implements OnClickListener{

    private FoodDBHelper mHelper;
    private EditText et_add;
    private int type;
    private LinearLayout ll_list;
    private SharedPreferences mShared;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        findViewById(R.id.btn_add).setOnClickListener(this);
        readSharedPreference();
        et_add = findViewById(R.id.et_add);
        ll_list = findViewById(R.id.ll_list);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //get the object of FoodDBHelper and open Write Link获取数据库帮助器对象并打开写连接
        mHelper = FoodDBHelper.getInstance(this, 1);
        mHelper.openReadLink();
        getFoodList();
        showFoodList();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //close read link关闭读连接
        mHelper.closeLink();
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.btn_add){
            String name = et_add.getText().toString();
            if(TextUtils.isEmpty(name)){
                Toast.makeText(this, "请输入食物名称",Toast.LENGTH_SHORT).show();
                return;
            }
            Food info = new Food();
            info.food_name = name;
            info.food_type = type;
            et_add.setText("");
            mHelper.insert(info);
            getFoodList();
            showFoodList();
        }
    }

    private void readSharedPreference(){
        //get object from Share.xml 获取共享参数
        SharedPreferences shared = getSharedPreferences("share", MODE_PRIVATE);
        type = shared.getInt("type", 0);
    }

    public void getFoodList(){
        foodList.clear();//clean and rest FoodList清楚并重设食物列表
        foodList = mHelper.query(String.format("type=%d", type));
    }

    private  ArrayList<Food> foodList = new ArrayList<>();//List od food食物列表

    //initial number of view初始化视图编号
    private int mBeginViewId = 0x7f24fff0;//any number 任意数字

    private void showFoodList(){
        ll_list.removeAllViews();
        if(foodList.size()>0){
            int id = 0;
            for(int i = 0; i < foodList.size(); i++) {
                Food info = foodList.get(i);
                LinearLayout ll_row = newLinearLayout();
                TextView tv_name = newTestView(info.food_name);
                ll_row.setId(mBeginViewId + i);
                ll_row.addView(tv_name);
                //register ll_row context menu注册食物行的上下文菜单
                unregisterForContextMenu(ll_row);//为避免重复注册，先注销
                registerForContextMenu(ll_row);//再注册
                //put the id and info to map添加视图ID与info的对映
                ll_row.setBackgroundResource(R.drawable.shape_rectangle);
                mFoodInfo.put(ll_row.getId(), info);
                ll_list.addView(ll_row);
            }
        }else{
            LinearLayout ll_row = newLinearLayout();
            TextView tv_name = newTestView("还没有输入食物呢 >*<");
            ll_row.addView(tv_name);
            ll_list.addView(ll_row);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //et menu layout获得菜单布局
        getMenuInflater().inflate(R.menu.menu_food_list, menu);
        return true;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.menu_delete_all){//click the delete_all item点击了“删除所有”
            mHelper.delete(String.format("type=%d", type));
            mFoodInfo.clear();
            getFoodList();
            showFoodList();
        }
        return true;
    }

    //create an HashMap to find the food by map声明一个查早食物讯息的映射
    private HashMap<Integer, Food> mFoodInfo = new HashMap<>();
    //create an object use to trigger context menu声明一个触发上下文菜单的对象
    private View mContextView;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        //save view of food,use to delete this view保存才食物的视图，用于删除视图
        mContextView = v;
        getMenuInflater().inflate(R.menu.menu_food, menu);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean onContextItemSelected(MenuItem item){
        Food info = mFoodInfo.get(mContextView.getId());
        if(info == null){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
        int id = item.getItemId();
        if(id == R.id.menu_delete){//click the delete item点击了“删除”
            //delete the food in database删除数据库里的食物
            mHelper.delete(String.format("rowid='%d'",info.rowid));
            //clear the view of food删除食物的视图
            ll_list.removeView(mContextView);
        }else if(id == R.id.menu_edit){
            //get the case of Application获得Application的实例
            MainApplication app = MainApplication.getInstance();
            //reset the value in Application修改实例中的变量
            //app.FoodLayout = mContextView;
            app.info = info;
            Intent intent = new Intent(AddFoodActivity.this, FoodEditActivity.class);
            intent.putExtra("food_name", info.food_name);
            //mHelper.delete(String.format("rowid='%d'",info.rowid));
            //request something from next page接收从下个页面返回的数据
            startActivityForResult(intent, 0);
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            //get String fool_name from return page从返回的页面得到食物名称
            String foodName = data.getStringExtra("food_name");
            MainApplication app = MainApplication.getInstance();
            Food info = app.info;
            info.food_name = foodName;
            mHelper.openWriteLink();
            mHelper.update(info);
        }
    }

    private LinearLayout newLinearLayout(){
        LinearLayout ll_new = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,5,5,5);
        ll_new.setLayoutParams(params);
        ll_new.setOrientation(LinearLayout.HORIZONTAL);
        ll_new.setBackgroundColor(Color.WHITE);
        ll_new.setPadding(10,10,10,10);
        return ll_new;
    }

    private TextView newTestView(String text){
        TextView tv_new = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv_new.setLayoutParams(params);
        tv_new.setText(text);
        tv_new.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        tv_new.setTextColor(Color.BLACK);
        tv_new.setGravity(Gravity.LEFT | Gravity.CENTER);
        return tv_new;
    }
}
