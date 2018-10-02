package app.insti.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import app.insti.R;

public class ImageFragment extends BaseFragment {

    private static final String TAG = ImageFragment.class.getSimpleName();
    private String image;
    int indexChosen;

    public static ImageFragment newInstance(String image, int index) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("image", image);
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "getArguments in ImageFragment" + getArguments());
        if (getArguments() != null) {
            image = getArguments().getString("image");
            indexChosen = getArguments().getInt("index", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        Picasso.get().load(image).into(imageView);
        return view;
    }
}