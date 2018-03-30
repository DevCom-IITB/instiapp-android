package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import in.ac.iitb.gymkhana.iitbapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CMSFragment extends BaseFragment {

    WebView wb;

    public CMSFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_cms, container, false);

        wb =(WebView) v.findViewById(R.id.WebView);
        wb.loadUrl("https://support.iitb.ac.in/support/login.jsp");

        WebSettings webSettings = wb.getSettings();
        webSettings.setJavaScriptEnabled(true);




        return v;
    }

}
