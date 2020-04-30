package com.android.jesse.biliparser.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.jesse.biliparser.R;
import com.android.jesse.biliparser.utils.DateUtil;
import com.android.jesse.biliparser.utils.ScreenManager;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @Description: bugly在线升级自定义Activity
 * @author: zhangshihao
 * @date: 2019/11/17 0017
 */
public class UpgradeActivity extends Activity {

    private static final String TAG = UpgradeActivity.class.getSimpleName();

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_version_name)
    TextView tv_version_name;
    @BindView(R.id.tv_package_size)
    TextView tv_package_size;
    @BindView(R.id.tv_upgrade_time)
    TextView tv_upgrade_time;
    @BindView(R.id.tv_upgrade_content)
    TextView tv_upgrade_content;
    @BindView(R.id.btn_upgrade)
    Button btn_upgrade;
    @BindView(R.id.btn_cancel)
    Button btn_cancel;

    private UpgradeInfo upgradeInfo;
    protected Activity mContext;
    private Unbinder mUnBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upgrade_activity);
        mUnBinder = ButterKnife.bind(this);
        mContext = this;
        ScreenManager.getInstance().setStatusBar(true,mContext);//设置沉浸式状态栏
        initEventAndData();
    }

    protected void initEventAndData() {
        upgradeInfo = Beta.getUpgradeInfo();

        tv_version_name.setText("版本: "+upgradeInfo.versionName);
        tv_package_size.setText("包大小: "+formatSize(upgradeInfo.fileSize));
        tv_upgrade_time.setText("更新时间: "+DateUtil.getFormatedDate(upgradeInfo.publishTime,"yyyy-MM-dd hh:mm:ss"));
        tv_upgrade_content.setText(upgradeInfo.newFeature);

        Beta.registerDownloadListener(new DownloadListener() {
            @Override
            public void onReceive(DownloadTask downloadTask) {
                updateBtn(downloadTask);
            }

            @Override
            public void onCompleted(DownloadTask downloadTask) {
                updateBtn(downloadTask);
            }

            @Override
            public void onFailed(DownloadTask downloadTask, int i, String s) {
                updateBtn(downloadTask);
            }
        });
    }

    private String formatSize(long target_size) {
        return Formatter.formatFileSize(mContext, target_size);
    }

    @OnClick({R.id.btn_upgrade,R.id.btn_cancel})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_upgrade:
                DownloadTask task = Beta.startDownload();
                updateBtn(task);
                if (task.getStatus() == DownloadTask.DOWNLOADING) {
                    finish();
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    public void updateBtn(DownloadTask task) {

        /*根据下载任务状态设置按钮*/
        switch (task.getStatus()) {
            case DownloadTask.INIT:
            case DownloadTask.DELETED:
            case DownloadTask.FAILED: {
                btn_upgrade.setText("开始下载");
            }
            break;
            case DownloadTask.COMPLETE: {
                btn_upgrade.setText("安装");
            }
            break;
            case DownloadTask.DOWNLOADING: {
                btn_upgrade.setText("暂停");
            }
            break;
            case DownloadTask.PAUSED: {
                btn_upgrade.setText("继续下载");
            }
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mUnBinder.unbind();
        Beta.unregisterDownloadListener();
    }
}
