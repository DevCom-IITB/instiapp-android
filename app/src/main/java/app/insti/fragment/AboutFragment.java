package app.insti.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import app.insti.R;

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
        CircleImageView varunimg = getActivity().findViewById(R.id.varunimg);
        CircleImageView sajalimg = getActivity().findViewById(R.id.sajalimg);
        CircleImageView nihalimg = getActivity().findViewById(R.id.nihalimg);
        CircleImageView ydidwaniaimg = getActivity().findViewById(R.id.ydidwaniaimg);
        CircleImageView cheekuimg = getActivity().findViewById(R.id.cheekuimg);
        CircleImageView sarthakimg = getActivity().findViewById(R.id.sarthakimg);
        CircleImageView sohamimg = getActivity().findViewById(R.id.sohamimg);
        CircleImageView mrunmayiimg = getActivity().findViewById(R.id.mrunmayiimg);
        CircleImageView owaisimg = getActivity().findViewById(R.id.owaisimg);
        CircleImageView hrushikeshimg = getActivity().findViewById(R.id.hrushikeshimg);
        CircleImageView yashkhemimg = getActivity().findViewById(R.id.yashkhemimg);
        CircleImageView bavishimg = getActivity().findViewById(R.id.bavishimg);
        CircleImageView mayuimg = getActivity().findViewById(R.id.mayuimg);
        CircleImageView tomarimg = getActivity().findViewById(R.id.tomarimg);
        CircleImageView bijoyimg = getActivity().findViewById(R.id.bijoyimg);
        CircleImageView dheerendraimg = getActivity().findViewById(R.id.dheerendraimg);
        CircleImageView ranveerimg = getActivity().findViewById(R.id.ranveerimg);
        CircleImageView amangourimg = getActivity().findViewById(R.id.amangourimg);
        CircleImageView wnccimg = getActivity().findViewById(R.id.wnccimg);

        Picasso.with(getContext()).load("https://insti.app/team-pics/varun.jpg").into(varunimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/sajal.jpg").into(sajalimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/nihal.jpg").into(nihalimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/ydidwania.jpg").into(ydidwaniaimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/cheeku.jpg").into(cheekuimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/sarthak.jpg").into(sarthakimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/soham.jpg").into(sohamimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/mrunmayi.jpg").into(mrunmayiimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/owais.jpg").into(owaisimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/hrushikesh.jpg").into(hrushikeshimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/yashkhem.jpg").into(yashkhemimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/bavish.jpg").into(bavishimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/mayu.jpg").into(mayuimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/tomar.jpg").into(tomarimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/bijoy.jpg").into(bijoyimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/dheerendra.jpg").into(dheerendraimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/ranveer.jpg").into(ranveerimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/amangour.jpg").into(amangourimg);
        Picasso.with(getContext()).load("https://insti.app/team-pics/wncc.jpg").into(wnccimg);





    }
}
