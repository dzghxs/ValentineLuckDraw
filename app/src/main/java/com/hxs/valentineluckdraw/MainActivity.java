package com.hxs.valentineluckdraw;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hxs.valentineluckdraw.utils.OpenFile;
import com.hxs.valentineluckdraw.weight.LuckPan;
import com.leon.lfilepickerlibrary.LFilePicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rl_bg)
    RelativeLayout rlBg;
    @BindView(R.id.btn_import)
    Button btnImport;
    @BindView(R.id.tv_name)
    TextView tvName;

    int REQUESTCODE_FROM_ACTIVITY = 1000;

    private List<String> namelist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        CheckPermissionInit();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getError(List<String> list) {
        Toast.makeText(this, "解析成功", Toast.LENGTH_SHORT).show();
        LuckPan luckPan = new LuckPan(this);
        rlBg.addView(luckPan);
        String[] mItemStrs = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            mItemStrs[i] = list.get(i);
        }
        luckPan.setItems(mItemStrs);
        init(luckPan);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.mipmap.ic_luckdrawstart);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp.height = 260;
        lp.width = 260;
        imageView.setLayoutParams(lp);
        rlBg.addView(imageView, lp);
        imageView.setOnClickListener(view -> luckPan.startAnim());
    }

    private void init(LuckPan v) {
        v.setLuckPanAnimEndCallBack(str -> tvName.setText(str));
    }


    private void parseXMLWithPull(String result) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(result));

            String name = "";

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                Log.d(TAG, "nodeName: " + nodeName);
                switch (eventType) {
                    case XmlPullParser.START_TAG://开始解析
                        if ("county".equals(nodeName)) {
                            name = parser.getAttributeValue(null, "name");
                            namelist.add(name);
                        }
                        break;

                    case XmlPullParser.END_TAG://完成解析
                        if ("county".equals(nodeName)) {
                            Log.i(TAG, "name: " + name);
                            namelist.add(name);
                        }
                        break;
                    default:
                        break;
                }
                try {
                    eventType = parser.next();
                } catch (Exception e) {

                }
            }
            HashSet h = new HashSet(namelist);
            namelist.clear();
            namelist.addAll(h);
            EventBus.getDefault().post(namelist);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证权限
     */
    public void CheckPermissionInit() {
        XXPermissions.with(this)
                .permission(Permission.Group.STORAGE)
                .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                .request(new OnPermission() {

                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        System.out.println(granted);
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        Toast.makeText(MainActivity.this, "必要权限未提供，app即将关闭", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                Toast.makeText(this, "开始解析外部数据", Toast.LENGTH_SHORT).show();
                //如果是文件选择模式，需要获取选择的所有文件的路径集合
                //List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//Constant.RESULT_INFO == "paths"
                List<String> list = data.getStringArrayListExtra("paths");
                //如果是文件夹选择模式，需要获取选择的文件夹路径
                String path = data.getStringExtra("path");
                parseXMLWithPull(OpenFile.getString(list.get(0), MainActivity.this));
            }
        }
    }

    @OnClick({R.id.btn_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_import:
                new LFilePicker()
                        .withActivity(MainActivity.this)
                        .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                        .withStartPath(Environment.getExternalStorageDirectory().getPath())//指定初始显示路径
                        .withIsGreater(false)//过滤文件大小 小于指定大小的文件
                        .withFileSize(500 * 1024)//指定文件大小为500K
                        .start();
                break;
        }
    }
}
