package app.insti.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import app.insti.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends BaseFragment {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("About");

        /* Map CircleImageView ids to image URLs */
        final Map<Integer, String> team = new HashMap<Integer, String>() {{
            put(R.id.varunimg, "varun.jpg");
            put(R.id.sajalimg, "sajal.jpg");
            put(R.id.nihalimg, "nihal.jpg");
            put(R.id.ydidwaniaimg, "ydidwania.jpg");
            put(R.id.cheekuimg, "cheeku.jpg");
            put(R.id.sarthakimg, "sarthak.jpg");
            put(R.id.sohamimg, "soham.jpg");
            put(R.id.maitreyaimg, "maitreya.jpg");
            put(R.id.mrunmayiimg, "mrunmayi.jpg");
            put(R.id.owaisimg, "owais.jpg");
            put(R.id.hrushikeshimg, "hrushikesh.jpg");
            put(R.id.yashkhemimg, "yashkhem.jpg");
            put(R.id.bavishimg, "bavish.jpg");
            put(R.id.mayuimg, "mayu.jpg");
            put(R.id.tomarimg, "tomar.jpg");
            put(R.id.bijoyimg, "bijoy.jpg");
            put(R.id.dheerendraimg, "dheerendra.jpg");
            put(R.id.ranveerimg, "ranveer.jpg");
            put(R.id.amangourimg, "amangour.jpg");
            put(R.id.wnccimg, "wncc.jpg");
            put(R.id.safwanimg, "mayu.jpg");
        }};

        /* Show team pics */
        for (final Map.Entry<Integer, String> entry : team.entrySet()) {
            CircleImageView circleImageView = getActivity().findViewById(entry.getKey());
            Picasso.get().load("https://insti.app/team-pics/" + entry.getValue()).resize(0, 300).into(circleImageView);
        }

        /* Map TextView ids to links */
        final Map<Integer, String> joinUs = new HashMap<Integer, String>() {{
            ;
            put(R.id.django, "https://github.com/wncc/IITBapp");
            put(R.id.android, "https://github.com/wncc/InstiApp");
            put(R.id.angular, "https://github.com/pulsejet/iitb-app-angular");
        }};

        for (final Map.Entry<Integer, String> entry : joinUs.entrySet()) {
            getActivity().findViewById(entry.getKey()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriUrl = Uri.parse(entry.getValue());
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);
                }
            });
        }
    }
}
