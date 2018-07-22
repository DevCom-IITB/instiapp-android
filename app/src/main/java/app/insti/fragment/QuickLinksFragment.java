package app.insti.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.insti.R;

public class QuickLinksFragment extends BaseFragment {

    public QuickLinksFragment() {
        // Required empty public constructor
    }

    public void onStart(){
        super.onStart();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Quick Links");

        TextView CMS =  getActivity().findViewById(R.id.button_CMS);
        TextView CMSMaint = getActivity().findViewById(R.id.button_CMSMaint);
        TextView CMSNet = getActivity().findViewById(R.id.button_CMSNet);
        TextView ASC = getActivity().findViewById(R.id.button_ASC);
        TextView ASCExt = getActivity().findViewById(R.id.button_ASCExt);
        TextView Moodle = getActivity().findViewById(R.id.button_Moodle);
        TextView Intern = getActivity().findViewById(R.id.button_Internship);
        TextView Placement = getActivity().findViewById(R.id.button_Placement);
        TextView Library = getActivity().findViewById(R.id.button_Library);
        TextView AcadCal = getActivity().findViewById(R.id.button_Acad_calendar);
        TextView AcadTime = getActivity().findViewById(R.id.button_Acad_timetable);
        TextView Holidays = getActivity().findViewById(R.id.button_Holidays);
        TextView Circulars = getActivity().findViewById(R.id.button_Circulars);
        TextView Courses = getActivity().findViewById(R.id.button_Courselist);
        TextView GPO = getActivity().findViewById(R.id.button_GPO);
        TextView CAMP = getActivity().findViewById(R.id.button_CAMP);
        TextView MSStore = getActivity().findViewById(R.id.button_MSStore);
        TextView BigHome = getActivity().findViewById(R.id.button_BigHomeCloud);
//      TextView FTP = getActivity().findViewById(R.id.button_FTP);
        TextView Intercom = getActivity().findViewById(R.id.button_Intercom);
        TextView Hospital = getActivity().findViewById(R.id.button_Hospital);
        TextView VPN = getActivity().findViewById(R.id.button_VPN);

        CMS.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://gymkhana.iitb.ac.in/cms_new/");  } });
        CMSMaint.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://support.iitb.ac.in");  } });
        CMSNet.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://help-cc.iitb.ac.in/");  } });
        ASC.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://asc.iitb.ac.in");  } });
        ASCExt.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://portal.iitb.ac.in/asc");  } });
        Moodle.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://moodle.iitb.ac.in");  } });
        Intern.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("http://placements.iitb.ac.in/internship/login.jsp");  } });
        Placement.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("http://placements.iitb.ac.in/placements/login.jsp");  } });
        Library.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("http://www.library.iitb.ac.in/");  } });
        AcadCal.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("http://www.iitb.ac.in/newacadhome/toacadcalender.jsp");  } });
        AcadTime.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("http://www.iitb.ac.in/newacadhome/timetable.jsp");  } });
        Holidays.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("http://www.iitb.ac.in/en/about-iit-bombay/iit-bombay-holidays-list");  } });
        Circulars.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("http://www.iitb.ac.in/newacadhome/circular.jsp");  } });
        Courses.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://portal.iitb.ac.in/asc/Courses");  } });
        GPO.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://gpo.iitb.ac.in");  } });
        CAMP.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://camp.iitb.ac.in/");  } });
        MSStore.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("http://msstore.iitb.ac.in/");  } });
        BigHome.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://home.iitb.ac.in/");  } });
//      FTP.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("ftp://ftp.iitb.ac.in/");  } });
        Intercom.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://portal.iitb.ac.in/TelephoneDirectory/");  } });
        Hospital.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("http://www.iitb.ac.in/hospital/");  } });
        VPN.setOnClickListener(new View.OnClickListener() {  public void onClick(View v) {  goToUrl("https://www.cc.iitb.ac.in/engservices/engaccessingiitffromoutside/19-vpn");  } });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quick_links, container, false);
    }

    public void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}
