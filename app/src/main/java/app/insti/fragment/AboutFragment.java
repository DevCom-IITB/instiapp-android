package app.insti.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import app.insti.R;
import app.insti.activity.MainActivity;
import app.insti.adapter.AboutCategoryAdapter;
import app.insti.api.model.AboutCategory;
import app.insti.api.model.AboutIndividual;

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

        AboutCategoryAdapter categoryAdapter = new AboutCategoryAdapter(new ArrayList<AboutCategory>() {{
            add(new AboutCategory("Core Developers", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("varunpatil", "Varun Patil", "varun.jpg"));
                add(new AboutIndividual("sajalnarang", "Sajal Narang", "sajal.jpg"));
            }}));
            add(new AboutCategory("Developers", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("160020012", "Mrunmayi Munkegar", "mrunmayi.jpg"));
                add(new AboutIndividual("160110009", "Owais Chunawala", "owais.jpg"));
                add(new AboutIndividual("hrushikeshbodas", "Hrushikesh Bodas", "hrushikesh.jpg"));
                add(new AboutIndividual("yashkhem", "Yash Khemchandani", "yashkhem.jpg"));
                add(new AboutIndividual("bavish.kulur", "Bavish Kulur", "bavish.jpg"));
                add(new AboutIndividual("mayubhattu", "Mayuresh Bhattu", "mayu.jpg"));
                add(new AboutIndividual("maitreya", "Maitreya Verma", "maitreya.jpg"));
                add(new AboutIndividual("safwankdb", "Mohd Safwan", "safwan.jpg"));
            }}));
            add(new AboutCategory("Design", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("150040007", "Soham Khadtare", "soham.jpg"));
            }}));
            add(new AboutCategory("Ideation", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("nihal111", "Nihal Singh", "nihal.jpg"));
                add(new AboutIndividual("ydidwania", "Yashwarshan Didwania", "ydidwania.jpg"));
                add(new AboutIndividual("kumar.ayush", "Kumar Ayush", "cheeku.jpg"));
                add(new AboutIndividual("16D110006", "Sarthak Khandelwal", "sarthak.jpg"));
            }}));
            add(new AboutCategory("Alumni", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("abhijit.tomar", "Abhijit Tomar", "tomar.jpg"));
                add(new AboutIndividual(null, "Bijoy Singh Kochar", "bijoy.jpg"));
                add(new AboutIndividual(null, "Dheerendra Rathor", "dheerendra.jpg"));
                add(new AboutIndividual(null, "Ranveer Aggarwal", "ranveer.jpg"));
                add(new AboutIndividual(null, "Aman Gour", "amangour.jpg"));
            }}));
            add(new AboutCategory("Contribute", new ArrayList<AboutIndividual>() {{
                add(new AboutIndividual("https://github.com/wncc/IITBapp", "Django API", "python.png"));
                add(new AboutIndividual("https://github.com/wncc/InstiApp", "Android App", "android.png"));
                add(new AboutIndividual("https://github.com/pulsejet/iitb-app-angular", "Angular PWA", "angular.png"));
            }}));
        }}, (MainActivity) getActivity());

        RecyclerView aboutRecyclerView = getActivity().findViewById(R.id.about_recycler_view);
        aboutRecyclerView.setAdapter(categoryAdapter);
        aboutRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
