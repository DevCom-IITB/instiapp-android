package app.insti.interfaces;

import android.content.Context;
import android.view.View.OnClickListener;

public interface Clickable {
    String getId();

    OnClickListener getOnClickListener(Context context);
}
