package com.example.menudemo;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**

 OptionMenu：选项菜单，android中最常见的菜单，通过Menu键来调用


 1）如何使用OptionMenu？
 答：非常简单，重写两个方法就好，其实这两个方法我们在创建项目的时候就会自动生成~ 他们分别是：

 public boolean onCreateOptionsMenu(Menu menu)：调用OptionMenu，在这里完成菜单初始化
 public boolean onOptionsItemSelected(MenuItem item)：菜单项被选中时触发，这里完成事件处理
 当然除了上面这两个方法我们可以重写外我们还可以重写这三个方法：

 public void onOptionsMenuClosed(Menu menu)：菜单关闭会调用该方法
 public boolean onPrepareOptionsMenu(Menu menu)：选项菜单显示前会调用该方法， 可在这里进行菜单的调整(动态加载菜单列表)
 public boolean onMenuOpened(int featureId, Menu menu)：选项菜单打开以后会调用这个方法
 而加载菜单的方式有两种，一种是直接通过编写菜单XML文件，然后调用： getMenuInflater().inflate(R.menu.menu_main, menu);
 加载菜单 或者通过代码动态添加，onCreateOptionsMenu的参数menu，调用add方法添加 菜单，add(菜单项的组号，ID，排序号，标题)，
 另外如果排序号是按添加顺序排序的话都填0即可！

 */

public class MainActivity extends AppCompatActivity {

    private TextView tv_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_test = (TextView) findViewById(R.id.tv_test);

        registerForContextMenu(tv_test);//注册上下文菜单

    }

    ///设置OptionMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.opt_menu_red:
                tv_test.setTextColor(Color.RED);
                break;
            case R.id.opt_menu_green:
                tv_test.setTextColor(Color.GREEN);
                break;
            case R.id.opt_menu_blue:
                tv_test.setTextColor(Color.BLUE);
                break;
            case R.id.opt_menu_yellow:
                tv_test.setTextColor(Color.YELLOW);
                break;
            case R.id.opt_menu_gray:
                tv_test.setTextColor(Color.GRAY);
                break;
            case R.id.opt_menu_cyan:
                tv_test.setTextColor(Color.CYAN);
                break;
            case R.id.opt_menu_black:
                tv_test.setTextColor(Color.BLACK);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    ///设置context menu
    //重写上下文菜单的创建方法
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflator = new MenuInflater(this);
        inflator.inflate(R.menu.menu_context, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //上下文菜单的点击事件
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu_blue:
                tv_test.setTextColor(Color.BLUE);
                break;
            case R.id.context_menu_green:
                tv_test.setTextColor(Color.GREEN);
                break;
            case R.id.context_menu_red:
                tv_test.setTextColor(Color.RED);
                break;
        }
        return super.onContextItemSelected(item);
    }
}