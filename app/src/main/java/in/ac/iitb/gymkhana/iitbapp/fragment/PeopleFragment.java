package in.ac.iitb.gymkhana.iitbapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v7.widget.SearchView;

import java.util.ArrayList;

import in.ac.iitb.gymkhana.iitbapp.PeopleSuggestionAdapter;
import in.ac.iitb.gymkhana.iitbapp.R;


public class PeopleFragment extends Fragment {
    View view;
    SearchView searchView;
    PeopleSuggestionAdapter adapter;
    ArrayList<String> suggestionList=new ArrayList<String>();
    ListView listView;
    public PeopleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_people, container, false);

        suggestionList.add("Web and Coding Club");
        suggestionList.add("Electronics and Robotics Club");
        suggestionList.add("Krittika");
        suggestionList.add("Maths and Physics Club");
        suggestionList.add("Aeromodelling Club");
        suggestionList.add("Literati");
        suggestionList.add("Roots");
        suggestionList.add("Staccato");
        suggestionList.add("Saaz");
        suggestionList.add("InSync");
        suggestionList.add("Pixels");
        suggestionList.add("Rang");
        suggestionList.add("FourthWall");
        suggestionList.add("WeSpeak and Vaani");
        suggestionList.add("SilverScreen");
        suggestionList.add("IIT-BBC");
        suggestionList.add("Design Club");
        suggestionList.add("LifeStyleClub");
        suggestionList.add("MoodIndigo");
        suggestionList.add("Techfest");
        suggestionList.add("SARC");
        suggestionList.add("Academic Council");

        listView= (ListView) view.findViewById(R.id.list_view);
        listView.setVisibility(View.GONE);

        searchView=(SearchView) view.findViewById(R.id.search);

        adapter=new PeopleSuggestionAdapter(suggestionList);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listView.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                listView.setVisibility(View.VISIBLE);
                adapter.getFilter().filter(newText);

                return false;
            }
        });
        //TODO SuggestionClickListener




        return  view;
    }
}
