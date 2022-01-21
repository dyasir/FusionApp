package com.fusion.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fusion.app.databinding.ActivityMainBinding;
import com.gyf.immersionbar.ImmersionBar;

@Route(path = App.THIRD_ROUTE_PATH)
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.white)
                .statusBarDarkFont(false, 0f)
                .init();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /**
         * 自定义事件上报Google示例
         * Bundle为事件内的参数
         * app_custom_report为自定义事件的名称
         */
        Bundle bundle = new Bundle();
        bundle.putString("app_package_id", getPackageName());
        bundle.putInt("app_user_id", 12);
        bundle.putFloat("app_pay_money", 30.26f);
        App.getInstance().reportToGoogle("app_custom_report", bundle);
    }
}