package app.insti.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.insti.R;
import app.insti.api.model.Venter;

/**
 * Created by Shivam Sharma on 25-09-2018.
 */

public class ImageViewPagerAdapter extends PagerAdapter {

    private List<String> images = new ArrayList<>();
    public Context context;
    public LayoutInflater inflater;

    public ImageViewPagerAdapter(Context context, List<String> images)
    {
        this.context = context;
        this.images = images;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ImageViewPagerAdapter(Context context, Venter.Complaint detailedComplaint)
    {
        this.context = context;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (String image: detailedComplaint.getImages()){
            images.add(image);
        }
    }

    @Override
    public int getCount() {
        if (images.size() == 0)
            return 1;
        else
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
        final ImageView imageView = imageLayout.findViewById(R.id.slidingImageView);

        if (images.size() != 0)
            Picasso.get().load(images.get(position)).into(imageView);
        else
            Picasso.get().load(R.drawable.baseline_photo_black_48).resize(500,500).into(imageView);

        view.addView(imageLayout, 0);
        return imageLayout;
    }
}