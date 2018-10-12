package app.insti.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.insti.R;
import app.insti.api.model.Venter;
import app.insti.fragment.AddImageFragment;
import app.insti.fragment.ImageFragment;

/**
 * Created by Shivam Sharma on 25-09-2018.
 */

public class ImageViewPagerAdapter extends PagerAdapter {

    private static final String TAG = ImageViewPagerAdapter.class.getSimpleName();
    private List<String> images = new ArrayList<>();
    Venter.Complaint detailedComplaint; //maybe not needed
    public Context context;
    public LayoutInflater inflater;

    /*public ImageViewPagerAdapter(FragmentManager fragmentManager, List<String> images)
    {
        super(fragmentManager);
        this.images = images;
    }

    public ImageViewPagerAdapter(FragmentManager fragmentManager, Venter.Complaint detailedComplaint)
    {
        super(fragmentManager);
        this.detailedComplaint = detailedComplaint;

        for (String image: detailedComplaint.getImages()){
            images.add(image);
        }
    }*/

    public ImageViewPagerAdapter(Context context, List<String> images)
    {
        this.context = context;
        this.images = images;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ImageViewPagerAdapter(Context context, Venter.Complaint detailedComplaint)
    {
        this.context = context;
        this.detailedComplaint = detailedComplaint;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (String image: detailedComplaint.getImages()){
            images.add(image);
        }
    }

    @Override
    public int getCount() {

        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position)
    {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.slidingImageView);

        if (!(images.get(position).isEmpty()))
            //imageView.setImageResource(Integer.parseInt(images.get(position)));
            Picasso.get().load(images.get(position)).into(imageView);
        else
            Picasso.get().load(images.get(position)).placeholder(context.getDrawable(R.drawable.ic_add_a_photo_black_24dp)).into(imageView);

        view.addView(imageLayout, 0);

        return imageLayout;
    }




    /*@Override
    public Fragment getItem(int position) {
        Log.i(TAG, "images = " + images.size());
        Log.i(TAG, "size = " + getCount());
        Log.i(TAG, "pos = " + position);

        if (images.size() == 0){
            Log.i(TAG,"calling 1");
            return new AddImageFragment();
        }else {
            Log.i(TAG,"calling 2");
            return ImageFragment.newInstance(images.get(position),position);
        }
    }*/
}