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

public class ReportActivity extends AppCompatActivity {

    TkActivityReportBinding binding;

    private ReportAdapter mReportAdapterContent;
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
        listContent.add("Nội dung tục tĩu");
        listContent.add("Thông tin sai lệch về các vấn đề thời sự");
        listContent.add("Tội ác");
        listContent.add("Quảng cáo spam, bán hàng giả");
        listContent.add("Lan truyền tin đồn");
        listContent.add("Bị nghi ngờ gian lận");
        listContent.add("Sự sỉ nhục");
        listContent.add("Nội dung không phải nguyên bản");
        listContent.add("Hành vi nguy hiểm");

        List<String> listYear = new ArrayList<>();
        listYear.add("Hành vi sai trái của trẻ vị thành niên");
        listYear.add("Nội dung không phù hợp cho trẻ vị thành niên xem");

        List<String> listOther = new ArrayList<>();
        listOther.add("Không thoải mái");
        listOther.add("Bị nghi ngờ tự làm hại bản thân");
        listOther.add("Thu hút lượt thích và chia sẻ");
        listOther.add("khác");

        mReportAdapterContent.setList(listContent);
        mReportAdapterYear.setList(listYear);
        mReportAdapterOther.setList(listOther);
    }

    private void initListener() {
        mReportAdapterContent.setOnItemClickListener((adapter, view, position) -> {
            if (ClickUtil.isFastClick()) return;

            Intent intent = new Intent(this, ReportDetailActivity.class);
            intent.putExtra("name", (String) adapter.getData().get(position));
            intent.putExtra("id", id);
            intent.putExtra("position", ReportActivity.this.position);
            startActivity(intent);
        });

        mReportAdapterYear.setOnItemClickListener((adapter, view, position) -> {
            if (ClickUtil.isFastClick()) return;

            Intent intent = new Intent(this, ReportDetailActivity.class);
            intent.putExtra("name", (String) adapter.getData().get(position));
            intent.putExtra("id", id);
            intent.putExtra("position", ReportActivity.this.position);
            startActivity(intent);
        });

        mReportAdapterOther.setOnItemClickListener((adapter, view, position) -> {
            if (ClickUtil.isFastClick()) return;

            Intent intent = new Intent(this, ReportDetailActivity.class);
            intent.putExtra("name", (String) adapter.getData().get(position));
            intent.putExtra("id", id);
            intent.putExtra("position", ReportActivity.this.position);
            startActivity(intent);
        });
    }
}
