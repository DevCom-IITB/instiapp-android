package app.insti;

import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.time.Instant;

public final class Helpers {
    public static final void loadImageWithPlaceholder(final ImageView imageView, final String url) {
        Picasso.get()
            .load(Constants.resizeImageUrl(url, 200))
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
}
