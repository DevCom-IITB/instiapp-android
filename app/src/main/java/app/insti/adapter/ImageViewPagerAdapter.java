package app.insti.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.insti.api.model.Venter;
import app.insti.fragment.AddImageFragment;
import app.insti.fragment.ImageFragment;

/**
 * Created by Shivam Sharma on 25-09-2018.
 */

public class ImageViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = ImageViewPagerAdapter.class.getSimpleName();
    private List<String> images = new ArrayList<>();

    public ImageViewPagerAdapter(FragmentManager fragmentManager, List<String> images) {
        super(fragmentManager);
        this.images = images;
    }

    public ImageViewPagerAdapter(FragmentManager fragmentManager, Venter.Complaint detailedComplaint){
        super(fragmentManager);
        images.addAll(detailedComplaint.getImages());
    }

    @Override
    public int getCount() {
        if (images.size() == 0)
            return 1;
        return images.size();
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "images = " + images.size());
        Log.i(TAG, "size = " + getCount());
        Log.i(TAG, "pos = " + position);

        if (images.size() == 0){
            Log.i(TAG,"calling 1");
            return new AddImageFragment();
        }else {
            Log.i(TAG,"calling 2");
            return ImageFragment.newInstance(images.get(position));
        }
    }
}