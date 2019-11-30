package app.insti.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;

import app.insti.R;
import app.insti.adapter.AboutAdapter;
import app.insti.api.model.AboutCategory;
import app.insti.api.model.AboutIndividual;

import static app.insti.api.model.AboutIndividual.TYPE_LINK;

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

        final Context context = getContext();

        AboutAdapter aboutAdapter = new AboutAdapter(new ArrayList<AboutCategory>() {{
            add(new AboutCategory("Core Developers", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("varunpatil", "Varun Patil", "varun.jpg"));
                add(new AboutIndividual("sajalnarang", "Sajal Narang", "sajal.jpg"));
                add(new AboutIndividual("harshith", "Harshith Goka", "harshith.jpg"));
            }}, context));
            add(new AboutCategory("Developers", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("160020012", "Mrunmayi Munkegar", "mrunmayi.jpg"));
                add(new AboutIndividual("160110009", "Owais Chunawala", "owais.jpg"));
                add(new AboutIndividual("hrushikeshbodas", "Hrushikesh Bodas", "hrushikesh.jpg"));
                add(new AboutIndividual("yashkhem", "Yash Khemchandani", "yashkhem.jpg"));
                add(new AboutIndividual("bavish.kulur", "Bavish Kulur", "bavish.jpg"));
                add(new AboutIndividual("mayubhattu", "Mayuresh Bhattu", "mayu.jpg"));
                add(new AboutIndividual("maitreya", "Maitreya Verma", "maitreya.jpg"));
                add(new AboutIndividual("safwankdb", "Mohd Safwan", "safwan.jpg"));
                add(new AboutIndividual(null, "Shivam Sharma", "sshivam95.jpg"));
            }}, context));
            add(new AboutCategory("Design", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("150040007", "Soham Khadtare", "soham.jpg"));
            }}, context));
            add(new AboutCategory("Ideation", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("nihal111", "Nihal Singh", "nihal.jpg"));
                add(new AboutIndividual("ydidwania", "Yashwarshan Didwania", "ydidwania.jpg"));
                add(new AboutIndividual("kumar.ayush", "Kumar Ayush", "cheeku.jpg"));
                add(new AboutIndividual("16D110006", "Sarthak Khandelwal", "sarthak.jpg"));
            }}, context));
            add(new AboutCategory("Alumni", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("abhijit.tomar", "Abhijit Tomar", "tomar.jpg"));
                add(new AboutIndividual(null, "Bijoy Singh Kochar", "bijoy.jpg"));
                add(new AboutIndividual(null, "Dheerendra Rathor", "dheerendra.jpg"));
                add(new AboutIndividual(null, "Ranveer Aggarwal", "ranveer.jpg"));
                add(new AboutIndividual(null, "Aman Gour", "amangour.jpg"));
            }}, context));
            add(new AboutCategory("Contribute", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("https://github.com/wncc/IITBapp", "Django API", "python.png", TYPE_LINK));
                add(new AboutIndividual("https://github.com/wncc/InstiApp", "Android App", "android.png", TYPE_LINK));
                add(new AboutIndividual("https://github.com/pulsejet/iitb-app-angular", "Angular PWA", "angular.png", TYPE_LINK));
            }}, context));
        }});

        RecyclerView aboutRecyclerView = getActivity().findViewById(R.id.about_recycler_view);
        aboutRecyclerView.setAdapter(aboutAdapter);

        FlexboxLayoutManager manager = new FlexboxLayoutManager(context, FlexDirection.ROW);
        manager.setJustifyContent(JustifyContent.CENTER);
        aboutRecyclerView.setLayoutManager(manager);
    }
}
