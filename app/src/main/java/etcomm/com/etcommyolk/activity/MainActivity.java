package etcomm.com.etcommyolk.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;


import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.EtcommApplication;
import etcomm.com.etcommyolk.fragment.FindFragment;
import etcomm.com.etcommyolk.fragment.AroundFragment;
import etcomm.com.etcommyolk.fragment.SportFragment;

/**
 * 主页 首页页面切换
 */
public class MainActivity extends Activity implements View.OnClickListener {
    /**
     * 发现
     */
    private FindFragment mFindFragment;
    /**
     * 健步
     */
    private SportFragment mSportsFragment;
    /**
     * 身边
     */
    private AroundFragment mSideFragment;
    /**
     * 进入发现模块
     */
    private CheckBox mFind;
    /**
     * 进入健步模块
     */
    private CheckBox mSports;
    /**
     * 进入发现模块
     */
    private CheckBox mSide;
    /**
     * 当前选中的index
     */
    private int mPage = 0;
    /**
     * 点击退出时记录的时间
     */
    private long exitTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //为空时初始化
        if (savedInstanceState == null) {
            initView();
        } else {
            mFindFragment = (FindFragment) getFragmentManager().findFragmentByTag("mFind");
            mSportsFragment = (SportFragment) getFragmentManager().findFragmentByTag("mSport");
            mSideFragment = (AroundFragment) getFragmentManager().findFragmentByTag("mSide");
            initView();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏DcareBaseAdapter
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    /**
     * 初始化控件
     */
    protected void initView() {
        /**
         * 发现
         */
        mFind = (CheckBox) findViewById(R.id.main_bottom_rb_find);
        mFind.setChecked(true);
        mFind.setOnClickListener(this);

        /**
         * 健步
         */
        mSports = (CheckBox) findViewById(R.id.main_bottom_rb_sport);
        mSports.setChecked(false);
        mSports.setOnClickListener(this);

        /**
         * 身边
         */
        mSide = (CheckBox) findViewById(R.id.main_bottom_rb_side);
        mSide.setChecked(false);
        mSide.setOnClickListener(this);

        //控制第一次进入的页面
        if (EtcommApplication.isFirstSetDefault) {
            EtcommApplication.isFirstSetDefault = false;
            //此代码在运行期间只执行一次
            selectFragment(0);//选择默认值
        }
    }

    /**
     * Fragment切换控制
     *
     * @param i 切换到第几个Fragment
     */
    public void selectFragment(int i) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragment(transaction);
        limitOnresume();
        switch (i) {
            case 0:
                FindFragment.limitResumeFind = true;
                if (mFindFragment == null) {
                    mFindFragment = new FindFragment();
                    transaction.add(R.id.mian_fragment, mFindFragment, "mFind");
                } else {
                    /**
                     * 生命周期控制不合理
                     *
                     * add + show 与创建的先后顺序相关
                     */
                    transaction.show(mFindFragment);
                }
                mPage = 0;
                mFind.setChecked(true);
                break;
            case 1:
                SportFragment.limitOnresumeSports = true;
                if (mSportsFragment == null) {
                    mSportsFragment = new SportFragment();
                    transaction.add(R.id.mian_fragment, mSportsFragment, "mSport");
                } else {
                    transaction.show(mSportsFragment);
                }
                mPage = 1;
                mSports.setChecked(true);
                break;
            case 2:
                AroundFragment.limitOnresumSide = true;
                if (mSideFragment == null) {
                    mSideFragment = new AroundFragment();
                    transaction.add(R.id.mian_fragment, mSideFragment, "mSide");
                } else {
                    transaction.show(mSideFragment);
                }
                mPage = 2;
                mSide.setChecked(true);
                break;
        }
        transaction.commit();
    }

    /**
     * 限制Fragment Onresume()执行次数
     * 修改变量就行
     */
    private void limitOnresume() {
        mFindFragment.limitResumeFind = false;
        mSportsFragment.limitOnresumeSports = false;
        mSideFragment.limitOnresumSide = false;
    }


    /**
     * 隐藏Fragment
     *
     * @param transaction Fragment管理器
     */
    private void hideFragment(FragmentTransaction transaction) {
        mFind.setChecked(false);
        mSports.setChecked(false);
        mSide.setChecked(false);

        if (mFindFragment != null) {
            transaction.hide(mFindFragment);
        }
        if (mSportsFragment != null) {
            transaction.hide(mSportsFragment);
        }
        if (mSideFragment != null) {
            transaction.hide(mSideFragment);
        }
    }

    /**
     * 返回键监听事件
     */
    @Override
    public void onBackPressed() {
        switch (mPage) {
            case 0:
            case 4:
                exit();
                break;
            case 1:
            case 2:
            case 3:
                selectFragment(0);
                mFind.setChecked(true);
                break;
        }
    }

    /**
     * 返回键退出控制
     */
    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_bottom_rb_find://发现
                selectFragment(0);
                break;
            case R.id.main_bottom_rb_sport://健步
                selectFragment(1);
                break;
            case R.id.main_bottom_rb_side://我的
                selectFragment(2);
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}
