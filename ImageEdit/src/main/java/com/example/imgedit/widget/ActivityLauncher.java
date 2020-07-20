package com.example.imgedit.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * Activity跳转封装类，把OnActivityResult方式改为Callback方式
 *
 */
public class ActivityLauncher {

    private static final String TAG = "ActivityLauncher";
    private Context mContext;
    /** V4兼容包下的Fragment */
    private RouterFragment mRouterFragment;
    private Intent MyIntent;

    public static ActivityLauncher init(Fragment fragment, Intent intent) {
        return init(fragment.getActivity());
    }

    public static ActivityLauncher init(FragmentActivity activity) {
        return new ActivityLauncher(activity);
    }

    public static ActivityLauncher init(Activity activity, Intent intent) {
        return new ActivityLauncher(activity,intent);
    }

    private ActivityLauncher(FragmentActivity activity) {
        mContext = activity;
        mRouterFragment = getRouterFragment(activity);
    }

    private ActivityLauncher(Activity activity,Intent intent) {
        MyIntent = intent;

        mContext = activity;
        mRouterFragment = getRouterFragment((FragmentActivity) activity);
    }

    private RouterFragment getRouterFragment(FragmentActivity activity) {
        RouterFragment routerFragment = findRouterFragment(activity);
        if (routerFragment == null) {
            routerFragment = RouterFragment.newInstance();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(routerFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return routerFragment;
    }

    private RouterFragment findRouterFragment(FragmentActivity activity) {
        return (RouterFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
    }

    public void startActivityForResult(int requestCode, Callback callback) {

        startActivityForResult(MyIntent, requestCode,callback);
    }

    public void startActivityForResult(Intent intent,int requestCode, Callback callback) {
        if (mRouterFragment != null) {
            mRouterFragment.startActivityForResult(intent,requestCode, callback);
        } else {
            throw new RuntimeException("please do init first!");
        }
    }

    public interface Callback {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}