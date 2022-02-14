package com.fusion.switchlib;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fusion.switchlib.http.HttpCallBack;
import com.fusion.switchlib.http.HttpRequest;
import com.fusion.switchlib.http.RetrofitFactory;
import com.fusion.switchlib.http.custom.AseUtils;
import com.fusion.switchlib.model.ConfigBean;
import com.fusion.switchlib.model.DataMgr;
import com.fusion.switchlib.model.FusionBean;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orhanobut.logger.Logger;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SwitchBaseActivity extends AppCompatActivity {

    private SwitchJumpListener switchJumpListener;
    private boolean switchJumpNow = false;  //立即更新
    private boolean switchJumpNotVietnamese = false;  //非越南地区立即更新

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataMgr.getInstance().getUser().setUdid(SwitchApplication.getInstance().getUDID());
        DataMgr.getInstance().getUser().setSysInfo(SwitchApplication.getInstance().getSysInfo());

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
            //匿名登录
//            FirebaseAuth mAuth = FirebaseAuth.getInstance();
//            mAuth.signInAnonymously()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
//                            Logger.e("Firebase匿名登录成功");
//                            FirebaseUser user = mAuth.getCurrentUser();

            /** 通过Firebase获取实时的API域名 **/
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("switch").document(SwitchApplication.getInstance().isProduct() ? "product" : "test")
                    .get()
                    .addOnCompleteListener(tasks -> {
                        if (tasks.isSuccessful() && tasks.getResult() != null) {
                            DocumentSnapshot documentSnapshot = tasks.getResult();
                            if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
                                Logger.i("-----Firebase同步数据成功-----\n  ----立即更新开关获取" +
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
                                                RetrofitFactory.NEW_URL = (SwitchApplication.getInstance().isProduct() ?
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
                                            RetrofitFactory.NEW_URL = (SwitchApplication.getInstance().isProduct() ?
                                                    "https://" : "http://") + documentSnapshot.getData().get(getPackageName());
                                        }
                                    }
                                }
                                getConfig();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Logger.e("-----Firebase同步数据失败: " + e.getMessage() + "-----");
                        getConfig();
                    });

//            db.collection("url").document(SwitchApplication.getInstance().isProduct() ? "product" : "test")
//                    .get()
//                    .addOnCompleteListener(tasks -> {
//                        if (tasks.isSuccessful() && tasks.getResult() != null) {
//                            DocumentSnapshot documentSnapshot = tasks.getResult();
//                            if (documentSnapshot.exists() && documentSnapshot.getData() != null) {
//                                Logger.i("----域名数据获取中----\n" + getPackageName() + ": " +
//                                        documentSnapshot.getData().get(getPackageName()) + "\napi_url: " +
//                                        documentSnapshot.getData().get("api_url") + "\n----域名数据获取结束----");
//                                if (TextUtils.isEmpty((String) documentSnapshot.getData().get(getPackageName()))) {
//                                    if (!TextUtils.isEmpty((String) documentSnapshot.getData().get("api_url")))
//                                        RetrofitFactory.NEW_URL = (String) documentSnapshot.getData().get("api_url");
//                                } else {
//                                    RetrofitFactory.NEW_URL = (String) documentSnapshot.getData().get(getPackageName());
//                                }
//                                getConfig();
//                            }
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Logger.e("-----Firebase同步数据失败: " + e.getMessage() + "-----");
//                        getConfig();
//                    });
//                        } else {
            // If sign in fails, display a message to the user.
//                            Logger.e("Firebase匿名登录失败: " + task.getException());
//                            getConfig();
//                        }
//                    });
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
        HttpRequest.getConfigs(this, SwitchApplication.getInstance().getUtm_source(), SwitchApplication.getInstance().getUtm_medium(),
                SwitchApplication.getInstance().getUtm_install_time(), SwitchApplication.getInstance().getUtm_version(), new HttpCallBack<ConfigBean>() {
                    @Override
                    public void onSuccess(ConfigBean configBean, String msg) {
                        configBean.setSysInfo(SwitchApplication.getInstance().getSysInfo());
                        configBean.setUdid(SwitchApplication.getInstance().getUDID());
                        configBean.setAppVersion(SwitchApplication.getInstance().getVerName());
                        DataMgr.getInstance().setUser(configBean);

//                        SwitchApplication.getInstance().setNotvi_update_enable(configBean.getNotvi_update_enable());
//                        SwitchApplication.getInstance().setIs_vi(configBean.getIs_vi());

                        HttpRequest.getIpCountry(SwitchBaseActivity.this, new HttpCallBack<String>() {
                            @Override
                            public void onSuccess(String s, String msg) {

                            }

                            @Override
                            public void onSuccess(String country, String countryCode, String query) {
                                if (country.equals("Vietnam") || country.equals("越南") || countryCode.equals("VI") || countryCode.equals("vi") ||
                                        countryCode.equals("Vi") || countryCode.equals("vI") || countryCode.equals("VN") || countryCode.equals("vn") ||
                                        countryCode.equals("Vn") || countryCode.equals("vN")) {
                                    if (switchJumpNow) {
                                        jumpB();
                                    } else {
                                        if (switchJumpListener != null)
                                            switchJumpListener.jumpPackageA();
                                    }
                                } else {
                                    if (switchJumpNotVietnamese) {
                                        jumpB();
                                    } else {
                                        if (switchJumpListener != null)
                                            switchJumpListener.jumpPackageA();
                                    }
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

                    @Override
                    public void onSuccess(String country, String countryCode, String query) {

                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg) {
                        //网络连接超时等意外情况，直接跳转A包
                        if (switchJumpListener != null)
                            switchJumpListener.jumpPackageA();
                    }
                });
    }

    private void jumpB() {
        HttpRequest.stateChange(this, 5, new HttpCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> strings, String msg) {
                //跳转B包
                if (switchJumpListener != null)
                    switchJumpListener.jumpPackageB();
            }

            @Override
            public void onSuccess(String country, String countryCode, String query) {

            }

            @Override
            public void onFail(int errorCode, String errorMsg) {
                //跳转B包
                if (switchJumpListener != null)
                    switchJumpListener.jumpPackageB();
            }
        });
    }

//    /**
//     * 获取融合APP配置信息
//     */
//    private void getFusionConfig() {
//        if (TextUtils.isEmpty(SPUtils.getString("fusion_jump"))) {
//            if (SwitchApplication.getInstance().getNotvi_update_enable().equals("1") ||
//                    SwitchApplication.getInstance().getIs_vi().equals("60")) {
//                HttpRequest.getFusion(this, new HttpCallBack<FusionBean>() {
//                    @Override
//                    public void onSuccess(FusionBean fusionBean, String msg) {
//                        if (fusionBean.isApp_change_enable()) {
//                            SPUtils.set("fusion_jump", "1");
//                            /** 立即更新或启动次数条件满足，跳转融合APP **/
//                            HttpRequest.stateChange(SwitchBaseActivity.this, 4, new HttpCallBack<List<String>>() {
//                                @Override
//                                public void onSuccess(List<String> list, String msg) {
//                                    //跳转B包
//                                    if (switchJumpListener != null)
//                                        switchJumpListener.jumpPackageB();
//                                }
//
//                                @Override
//                                public void onSuccess(String country, String countryCode, String query) {
//
//                                }
//
//                                @Override
//                                public void onFail(int errorCode, String errorMsg) {
//                                    //跳转B包
//                                    if (switchJumpListener != null)
//                                        switchJumpListener.jumpPackageB();
//                                }
//                            });
//                        } else {
//                            //是否立即更新为融合APP
//                            if (fusionBean.getApp_start_number() == 0 || SPUtils.getInteger("app_open_cout") <= fusionBean.getApp_start_number()) {
//                                //跳转B包
//                                if (switchJumpListener != null)
//                                    switchJumpListener.jumpPackageA();
//                            } else {
//                                SPUtils.set("fusion_jump", "1");
//                                /** 立即更新或启动次数条件满足，跳转融合APP **/
//                                HttpRequest.stateChange(SwitchBaseActivity.this, fusionBean.isApp_change_enable() ? 4 : 5, new HttpCallBack<List<String>>() {
//                                    @Override
//                                    public void onSuccess(List<String> list, String msg) {
//                                        //跳转B包
//                                        if (switchJumpListener != null)
//                                            switchJumpListener.jumpPackageB();
//                                    }
//
//                                    @Override
//                                    public void onSuccess(String country, String countryCode, String query) {
//
//                                    }
//
//                                    @Override
//                                    public void onFail(int errorCode, String errorMsg) {
//                                        //跳转B包
//                                        if (switchJumpListener != null)
//                                            switchJumpListener.jumpPackageB();
//                                    }
//                                });
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onSuccess(String country, String countryCode, String query) {
//
//                    }
//
//                    @Override
//                    public void onFail(int errorCode, String errorMsg) {
//                        //网络连接超时等意外情况，直接跳转A包
//                        if (switchJumpListener != null)
//                            switchJumpListener.jumpPackageA();
//                    }
//                });
//            } else {
//                //非越南地区，并且没打开融合开关，直接跳转A包
//                if (switchJumpListener != null)
//                    switchJumpListener.jumpPackageA();
//            }
//        } else {
//            //跳转B包
//            if (switchJumpListener != null)
//                switchJumpListener.jumpPackageB();
//        }
//    }
}
