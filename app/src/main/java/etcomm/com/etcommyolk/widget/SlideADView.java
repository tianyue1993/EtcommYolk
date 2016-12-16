package etcomm.com.etcommyolk.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import etcomm.com.etcommyolk.R;
import etcomm.com.etcommyolk.entity.RecommendItems;


/**
 * 广告图片自动轮播控件</br>
 * <p/>
 * <pre>
 *   集合ViewPager和指示器的一个轮播控件，主要用于一般常见的广告图片轮播，具有自动轮播和手动轮播功能
 *   使用：只需在xml文件中使用{@code <com.minking.imagecycleview.ImageCycleView/>} ，
 *   然后在页面中调用  {@link #setImageResources(ArrayList, SlideADViewListener) }即可!
 *
 *   另外提供{@link #startImageCycle() } \ {@link #pushImageCycle() }两种方法，用于在Activity不可见之时节省资源；
 *   因为自动轮播需要进行控制，有利于内存管理
 * </pre>
 */
public class SlideADView extends LinearLayout {

    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 图片轮播视图
     */
    private ViewPager mAdvPager = null;

    /**
     * 滚动图片视图适配器
     */
    private ImageCycleAdapter mAdvAdapter;

    /**
     * 图片轮播指示器控件
     */
    private ViewGroup mGroup;

    /**
     * 图片轮播指示器-个图
     */
    private ImageView mImageView = null;

    /**
     * 滚动图片指示器-视图列表
     */
    private ImageView[] mImageViews = null;

    /**
     * 图片滚动当前图片下标
     */
    private int mImageIndex = 0;

    /**
     * 手机密度
     */
    private float mScale;

    /**
     * @param context
     */
    public SlideADView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public SlideADView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScale = context.getResources().getDisplayMetrics().density;
        LayoutInflater.from(context).inflate(R.layout.layout_slide_ad_view, this);
        mAdvPager = (ViewPager) findViewById(R.id.adv_pager);
        mAdvPager.setOnPageChangeListener(new GuidePageChangeListener());
        mAdvPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // 开始图片滚动
                        startImageTimerTask();
                        break;
                    default:
                        // 停止图片滚动
                        stopImageTimerTask();
                        break;
                }
                return false;
            }
        });
        // 滚动图片右下指示器视图
        mGroup = (ViewGroup) findViewById(R.id.viewGroup);
    }

    /**
     * 装填图片数据
     *
     * @param ads
     * @param slideADViewListener
     */
    public void setImageResources(ArrayList<RecommendItems> ads, SlideADViewListener slideADViewListener) {
        // 清除所有子视图
        mGroup.removeAllViews();
        // 图片广告数量
        final int imageCount = ads.size();
        mImageViews = new ImageView[imageCount];
        for (int i = 0; i < imageCount; i++) {
            mImageView = new ImageView(mContext);
            int imageParams = (int) (mScale * 20 + 0.5f);// XP与DP转换，适应不同分辨率
            int imagePadding = (int) (mScale * 5 + 0.5f);
            mImageView.setLayoutParams(new LayoutParams(imageParams, imageParams));
            mImageView.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i].setImageResource(R.mipmap.dot_focus);
            } else {
                mImageViews[i].setImageResource(R.mipmap.dot_blur);
            }
            mGroup.addView(mImageViews[i]);
        }
        mAdvAdapter = new ImageCycleAdapter(mContext, ads, slideADViewListener);
        mAdvPager.setAdapter(mAdvAdapter);
        startImageTimerTask();
    }

    /**
     * 开始轮播(手动控制自动轮播与否，便于资源控制)
     */
    public void startImageCycle() {
        startImageTimerTask();
    }

    /**
     * 暂停轮播——用于节省资源
     */
    public void pushImageCycle() {
        stopImageTimerTask();
    }

    /**
     * 开始图片滚动任务
     */
    private void startImageTimerTask() {
        stopImageTimerTask();
        // 图片每3秒滚动一次
        mHandler.postDelayed(mImageTimerTask, 3000);
    }

    /**
     * 停止图片滚动任务
     */
    private void stopImageTimerTask() {
        mHandler.removeCallbacks(mImageTimerTask);
    }

    private Handler mHandler = new Handler();

    /**
     * 图片自动轮播Task
     */
    private Runnable mImageTimerTask = new Runnable() {

        @Override
        public void run() {
            if (mImageViews != null) {
                // 下标等于图片列表长度说明已滚动到最后一张图片,重置下标
                if ((++mImageIndex) == mImageViews.length) {
                    mImageIndex = 0;
                }
                mAdvPager.setCurrentItem(mImageIndex);
            }
        }
    };

    /**
     * 轮播图片状态监听器
     */
    private final class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE)
                startImageTimerTask(); // 开始下次计时
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int index) {
            // 设置当前显示的图片下标
            mImageIndex = index;
            // 设置图片滚动指示器背景
            mImageViews[index].setImageResource(R.mipmap.dot_focus);
            for (int i = 0; i < mImageViews.length; i++) {
                if (index != i) {
                    mImageViews[i].setImageResource(R.mipmap.dot_blur);
                }
            }

        }

    }

    private class ImageCycleAdapter extends PagerAdapter {

        /**
         * 图片视图缓存列表
         */
        private ArrayList<SimpleDraweeView> mImageViewCacheList;

        /**
         * 图片资源列表
         */
        private ArrayList<RecommendItems> mAdList = new ArrayList<RecommendItems>();

        /**
         * 广告图片点击监听器
         */
        private SlideADViewListener mSlideADViewListener;

        private Context mContext;

        public ImageCycleAdapter(Context context, ArrayList<RecommendItems> adList, SlideADViewListener slideADViewListener) {
            mContext = context;
            mAdList = adList;
            mSlideADViewListener = slideADViewListener;
            mImageViewCacheList = new ArrayList<SimpleDraweeView>();
        }

        @Override
        public int getCount() {
            return mAdList.size();
        } 

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final RecommendItems ad = mAdList.get(position);
            SimpleDraweeView imageView;
            if (mImageViewCacheList.isEmpty()) {
                imageView = new SimpleDraweeView(mContext);
                imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                imageView = mImageViewCacheList.remove(0);
            }
            // 设置图片点击监听
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                
                public void onClick(View v) {
                    mSlideADViewListener.onImageClick(ad, v);
                }
            });
            imageView.setTag(ad);
            container.addView(imageView);
            mSlideADViewListener.displayImage(ad, imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            SimpleDraweeView view = (SimpleDraweeView) object;
            container.removeView(view);
            mImageViewCacheList.add(view);
        }

    }

    /**
     * 轮播控件的监听事件
     */
    public interface SlideADViewListener {

        /**
         * 加载图片资源
         *
         * @param ad
         * @param imageView
         */
        void displayImage(RecommendItems ad, SimpleDraweeView imageView);

        /**
         * 单击图片事件
         *
         * @param ad
         * @param imageView
         */
        void onImageClick(RecommendItems ad, View imageView);
    }

}
