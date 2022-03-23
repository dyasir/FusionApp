package com.shortvideo.lib;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shortvideo.lib.common.http.AseUtils;
import com.shortvideo.lib.common.http.HttpCallBack;
import com.shortvideo.lib.common.http.HttpRequest;
import com.shortvideo.lib.common.http.RetrofitFactory;
import com.shortvideo.lib.model.DataMgr;
import com.shortvideo.lib.utils.SPUtils;

public class SwitchBaseActivity extends AppCompatActivity {

    private SwitchJumpListener switchJumpListener;
    private boolean switchJumpNow = false;  //立即更新
    private boolean switchJumpNotVietnamese = false;  //非越南地区立即更新

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataMgr.getInstance().getUser().setUdid(VideoApplication.getInstance().getUDID());
        DataMgr.getInstance().getUser().setSysInfo(VideoApplication.getInstance().getSysInfo());

        new Handler().postDelayed(this::syncFirebase, 350);
    }

    protected void initSwitchJumpListener(SwitchJumpListener switchJumpListener) {
        this.switchJumpListener = switchJumpListener;
    }

    /**
     * 同步Firebase数据
     */
    private void syncFirebase() {
        if (TextUtils.isEmpty(SPUtils.getString("fusion_jump"))) {
            /** 通过Firebase获取实时的API域名 **/
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("switch").document(VideoApplication.getInstance().isProduct() ? "product" : "test")
                    .get()
                    .addOnCompleteListener(tasks -> {
                        if (tasks.isSuccessful()) {
                            if (tasks.getResult() != null) {
                                DocumentSnapshot documentSnapshot = tasks.getResult();
                                if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                                    Log.i("result", "-----Firebase同步数据成功-----\n  ----立即更新开关获取" +
                                            (TextUtils.isEmpty((String) documentSnapshot.getData().get("update_immediately")) ? "失败----" :
                                                    ("成功----\n" + "    ---立即更新状态: " + (documentSnapshot.getData().get("update_immediately").equals(AseUtils.AES_KAI) ?
                                                            "开" : "关") + "---")) + "\n  ----非越南更新开关获取" +
                                            (TextUtils.isEmpty((String) documentSnapshot.getData().get("not_vietnamese")) ? "失败----" :
                                                    ("成功----\n" + "    ---非越南更新状态: " + (documentSnapshot.getData().get("not_vietnamese").equals(AseUtils.AES_KAI) ?
                                                            "开" : "关") + "---")) + "\n  ----包指定域名获取" +
                                            (TextUtils.isEmpty((String) documentSnapshot.getData().get(getPackageName())) ? ("失败----\n" +
                                                    "    ---通用域名: " + documentSnapshot.getData().get("api_url")) :
                                                    ("成功----\n" + "    ---包指定域名: " + documentSnapshot.getData().get(getPackageName()))) +
                                            "\n-----开关数据获取完毕-----");
                                    Log.i("result", documentSnapshot.getData().get("update_immediately") + "\n" + documentSnapshot.getData().get("not_vietnamese"));
                                    switchJumpNow = !TextUtils.isEmpty((String) documentSnapshot.getData().get("update_immediately")) &&
                                            documentSnapshot.getData().get("update_immediately").equals(AseUtils.AES_KAI);
                                    switchJumpNotVietnamese = !TextUtils.isEmpty((String) documentSnapshot.getData().get("not_vietnamese")) &&
                                            documentSnapshot.getData().get("not_vietnamese").equals(AseUtils.AES_KAI);
                                    if (TextUtils.isEmpty((String) documentSnapshot.getData().get(getPackageName()))) {
                                        if (!TextUtils.isEmpty((String) documentSnapshot.getData().get("api_url"))) {
                                            if ((((String) documentSnapshot.getData().get("api_url")).startsWith("http://") ||
                                                    ((String) documentSnapshot.getData().get("api_url")).startsWith("https://")) &&
                                                    ((String) documentSnapshot.getData().get("api_url")).endsWith("/")) {
                                                RetrofitFactory.NEW_URL = (String) documentSnapshot.getData().get("api_url");
                                            } else {
                                                if (((String) documentSnapshot.getData().get("api_url")).startsWith("http://") ||
                                                        ((String) documentSnapshot.getData().get("api_url")).startsWith("https://")) {
                                                    RetrofitFactory.NEW_URL = documentSnapshot.getData().get("api_url") + "/";
                                                } else {
                                                    RetrofitFactory.NEW_URL = (VideoApplication.getInstance().isProduct() ?
                                                            "https://" : "http://") + documentSnapshot.getData().get("api_url");
                                                }
                                            }
                                        }
                                    } else {
                                        if ((((String) documentSnapshot.getData().get(getPackageName())).startsWith("http://") ||
                                                ((String) documentSnapshot.getData().get(getPackageName())).startsWith("https://")) &&
                                                ((String) documentSnapshot.getData().get(getPackageName())).endsWith("/")) {
                                            RetrofitFactory.NEW_URL = (String) documentSnapshot.getData().get(getPackageName());
                                        } else {
                                            if (((String) documentSnapshot.getData().get(getPackageName())).startsWith("http://") ||
                                                    ((String) documentSnapshot.getData().get(getPackageName())).startsWith("https://")) {
                                                RetrofitFactory.NEW_URL = documentSnapshot.getData().get(getPackageName()) + "/";
                                            } else {
                                                RetrofitFactory.NEW_URL = (VideoApplication.getInstance().isProduct() ?
                                                        "https://" : "http://") + documentSnapshot.getData().get(getPackageName());
                                            }
                                        }
                                    }
                                }
                            }
                            getConfig();
                        }
                    })
                    .addOnFailureListener(e -> getConfig());
        } else {
            //跳转B包
            if (switchJumpListener != null)
                switchJumpListener.jumpPackageB();
        }
    }

    /**
     * 获取配置信息
     */
    private void getConfig() {
        if (switchJumpNow) {
            if (switchJumpNotVietnamese) {
                if (switchJumpListener != null)
                    switchJumpListener.jumpPackageB();
            } else {
                HttpRequest.getIpCountry(SwitchBaseActivity.this, new HttpCallBack<String>() {

                    @Override
                    public void onSuccess(String country, String countryCode) {
                        if (country.equals("Vietnam") || country.equals("越南") || countryCode.equals("VI") || countryCode.equals("vi") ||
                                countryCode.equals("Vi") || countryCode.equals("vI") || countryCode.equals("VN") || countryCode.equals("vn") ||
                                countryCode.equals("Vn") || countryCode.equals("vN")) {
                            if (switchJumpListener != null)
                                switchJumpListener.jumpPackageB();
                        } else {
                            if (switchJumpListener != null)
                                switchJumpListener.jumpPackageA();
                        }
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        //网络连接超时等意外情况，直接跳转A包
                        if (switchJumpListener != null)
                            switchJumpListener.jumpPackageA();
                    }
                });
            }
        } else {
            if (switchJumpListener != null)
                switchJumpListener.jumpPackageA();
        }
    }
}
