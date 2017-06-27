package in.ac.iitb.gymkhana.iitbapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import in.ac.iitb.gymkhana.iitbapp.R;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.view.View.VISIBLE;


public class AddEventFragment extends Fragment {
    Button createEvent;
    CheckBox publicCheckBox;
    EditText date;
    EditText time;
    EditText venue;
    EditText details;
    ImageView eventImage;
    ImageButton imageButton;
    View view;


    public AddEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container.removeAllViews();

        view = inflater.inflate(R.layout.fragment_add_event, container, false);
        createEvent = (Button) view.findViewById(R.id.button_createEvent);
        publicCheckBox = (CheckBox) view.findViewById(R.id.cb_public);
        date = (EditText) view.findViewById(R.id.et_date);
        venue = (EditText) view.findViewById(R.id.et_venue);
        time = (EditText) view.findViewById(R.id.et_time);
        details = (EditText) view.findViewById(R.id.et_eventDetails);
        eventImage = (ImageView) view.findViewById(R.id.iv_eventImage);
        imageButton = (ImageButton) view.findViewById(R.id.ib_eventImage);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO (1) upload image to server
            }
        });
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO (2) save event
            }
        });
        return view;
    }
    //TODO(3) configure onBackPressed

}
