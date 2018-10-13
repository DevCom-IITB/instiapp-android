package app.insti.interfaces;

import android.content.Context;
import android.view.View.OnClickListener;

public interface Clickable {
    OnClickListener getOnClickListener(Context context);
}
