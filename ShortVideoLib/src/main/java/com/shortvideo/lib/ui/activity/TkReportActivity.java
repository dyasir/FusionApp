package com.shortvideo.lib.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gyf.immersionbar.ImmersionBar;
import com.shortvideo.lib.R;
import com.shortvideo.lib.databinding.TkActivityReportBinding;
import com.shortvideo.lib.ui.adapter.ReportAdapter;
import com.shortvideo.lib.utils.ActivityManager;
import com.shortvideo.lib.utils.ClickUtil;

import java.util.ArrayList;
import java.util.List;

public class TkReportActivity extends AppCompatActivity {

    TkActivityReportBinding binding;

    private ReportAdapter mReportAdapterContent;
    private ReportAdapter mReportAdapterMoney;
    private ReportAdapter mReportAdapterYear;
    private ReportAdapter mReportAdapterOther;

    private int id;
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.white)
                .keyboardEnable(true)
                .statusBarDarkFont(true, 0f)
                .init();

        binding = TkActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityManager.getAppInstance().addActivity(this);

        initView();
        initData();
        initListener();
    }

    protected void initView() {
        id = getIntent().getIntExtra("id", 0);
        position = getIntent().getIntExtra("position", 0);

        mReportAdapterContent = new ReportAdapter(new ArrayList<>());
        binding.recycleContent.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.recycleContent.setAdapter(mReportAdapterContent);

        mReportAdapterMoney = new ReportAdapter(new ArrayList<>());
        binding.recycleMoney.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.recycleMoney.setAdapter(mReportAdapterMoney);

        mReportAdapterYear = new ReportAdapter(new ArrayList<>());
        binding.recycleYear.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.recycleYear.setAdapter(mReportAdapterYear);

        mReportAdapterOther = new ReportAdapter(new ArrayList<>());
        binding.recycleOther.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.recycleOther.setAdapter(mReportAdapterOther);
    }

    private void initData() {
        List<String> listContent = new ArrayList<>();
        listContent.add(getString(R.string.tk_report_one_1));
        listContent.add(getString(R.string.tk_report_one_2));
        listContent.add(getString(R.string.tk_report_one_3));
        listContent.add(getString(R.string.tk_report_one_4));
        listContent.add(getString(R.string.tk_report_one_5));
        listContent.add(getString(R.string.tk_report_one_6));
        listContent.add(getString(R.string.tk_report_one_7));
        listContent.add(getString(R.string.tk_report_one_8));
        listContent.add(getString(R.string.tk_report_one_9));
        listContent.add(getString(R.string.tk_report_one_10));

        List<String> listMoney = new ArrayList<>();
        listMoney.add(getString(R.string.tk_report_two_1));
        listMoney.add(getString(R.string.tk_report_two_2));

        List<String> listYear = new ArrayList<>();
        listYear.add(getString(R.string.tk_report_three_1));
        listYear.add(getString(R.string.tk_report_three_2));

        List<String> listOther = new ArrayList<>();
        listOther.add(getString(R.string.tk_report_four_1));
        listOther.add(getString(R.string.tk_report_four_2));
        listOther.add(getString(R.string.tk_report_four_3));
        listOther.add(getString(R.string.tk_report_four_4));

        mReportAdapterContent.setList(listContent);
        mReportAdapterMoney.setList(listMoney);
        mReportAdapterYear.setList(listYear);
        mReportAdapterOther.setList(listOther);
    }

    private void initListener() {
        mReportAdapterContent.setOnItemClickListener((adapter, view, position) -> {
            if (ClickUtil.isFastClick()) return;

            Intent intent = new Intent(this, TkReportDetailActivity.class);
            intent.putExtra("name", (String) adapter.getData().get(position));
            intent.putExtra("id", id);
            intent.putExtra("position", TkReportActivity.this.position);
            startActivity(intent);
        });

        mReportAdapterMoney.setOnItemClickListener((adapter, view, position) -> {
            if (ClickUtil.isFastClick()) return;

            Intent intent = new Intent(this, TkReportDetailActivity.class);
            intent.putExtra("name", (String) adapter.getData().get(position));
            intent.putExtra("id", id);
            intent.putExtra("position", TkReportActivity.this.position);
            startActivity(intent);
        });

        mReportAdapterYear.setOnItemClickListener((adapter, view, position) -> {
            if (ClickUtil.isFastClick()) return;

            Intent intent = new Intent(this, TkReportDetailActivity.class);
            intent.putExtra("name", (String) adapter.getData().get(position));
            intent.putExtra("id", id);
            intent.putExtra("position", TkReportActivity.this.position);
            startActivity(intent);
        });

        mReportAdapterOther.setOnItemClickListener((adapter, view, position) -> {
            if (ClickUtil.isFastClick()) return;

            Intent intent = new Intent(this, TkReportDetailActivity.class);
            intent.putExtra("name", (String) adapter.getData().get(position));
            intent.putExtra("id", id);
            intent.putExtra("position", TkReportActivity.this.position);
            startActivity(intent);
        });
    }
}
