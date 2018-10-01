package app.insti;

import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import app.insti.api.model.Event;

public final class Utils {
    public static UpdatableList<Event> eventCache = new UpdatableList<>();

    public static final void loadImageWithPlaceholder(final ImageView imageView, final String url) {
        Picasso.get()
            .load(resizeImageUrl(url))
            .into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Picasso.get()
                            .load(url)
                            .placeholder(imageView.getDrawable())
                            .into(imageView);
                }
                @Override
                public void onError(Exception ex) {}
            });
    }

    public static final String resizeImageUrl(String url) {
        return resizeImageUrl(url, 200);
    }

    public static final String resizeImageUrl(String url, Integer dim) {
        if (url == null) { return url; }
        return url.replace("api.insti.app/static/", "img.insti.app/static/" + dim.toString() + "/");
    }
}
