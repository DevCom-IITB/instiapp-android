package app.insti.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import app.insti.R;
import app.insti.ShareURLMaker;
import app.insti.Utils;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.fragment.EventFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BodyHeadViewHolder  extends RecyclerView.ViewHolder {
    private TextView bodyName;
    private TextView bodySubtitle;
    private TextView bodyDescription;
    private ImageButton webBodyButton;
    private ImageButton shareBodyButton;
    private final Button followButton;

    public BodyHeadViewHolder(View itemView) {
        super(itemView);
        bodyName = itemView.findViewById(R.id.body_name);
        bodySubtitle = itemView.findViewById(R.id.body_subtitle);
        bodyDescription = itemView.findViewById(R.id.body_description);
        webBodyButton = itemView.findViewById(R.id.web_body_button);
        shareBodyButton = itemView.findViewById(R.id.share_body_button);
        followButton = itemView.findViewById(R.id.follow_button);
    }

    private void setupFollowButton(Context context, Body body) {
        Utils.setupFollowButton(context, followButton, body.getBodyUserFollows());
        followButton.setText(EventFragment.getCountBadgeSpannable("FOLLOW", body.getBodyFollowersCount()));
    }

    public void bindView(final Body body, final Fragment fragment) {
        /* Set body information */
        bodyName.setText(body.getBodyName());
        bodySubtitle.setText(body.getBodyShortDescription());

        /* Return if it's a min body */
        if (body.getBodyDescription() == null) {
            return;
        }

        Utils.getMarkwon().setMarkdown(bodyDescription, body.getBodyDescription());

        /* Check if user is already following
         * Initialize follow button */
        setupFollowButton(fragment.getContext(), body);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
                retrofitInterface.updateBodyFollowing(Utils.getSessionIDHeader(), body.getBodyID(), body.getBodyUserFollows() ? 0 : 1).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            body.setBodyUserFollows(!body.getBodyUserFollows());
                            body.setBodyFollowersCount(body.getBodyUserFollows()? body.getBodyFollowersCount()+1:body.getBodyFollowersCount()-1);
                            setupFollowButton(fragment.getContext(), body);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(fragment.getContext(), "Network Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        /* Initialize web button */
        if (body.getBodyWebsiteURL() != null && !body.getBodyWebsiteURL().isEmpty()) {
            webBodyButton.setVisibility(View.VISIBLE);
            webBodyButton.setOnClickListener(new View.OnClickListener() {
                String bodywebURL = body.getBodyWebsiteURL();

                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bodywebURL));
                    fragment.startActivity(browserIntent);
                }
            });
        }

        /* Initialize share button */
        shareBodyButton.setOnClickListener(new View.OnClickListener() {
            String shareUrl = ShareURLMaker.getBodyURL(body);

            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, shareUrl);
                fragment.startActivity(Intent.createChooser(i, "Share URL"));
            }
        });
    }
}
