package in.ac.iitb.gymkhana.iitbapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by mrunz on 15/7/17.
 */

public class AddEventAdapter extends BaseAdapter {

    LayoutInflater inflater;
    public int publicStatus = 0;
    int permission = 0;
    public String location = "";

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case 0: {
                    convertView = inflater.inflate(R.layout.advanced_options_view, null);
                    CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.cb_public);
                    if (checkBox.isChecked()) publicStatus = 1;
                    break;
                }
                case 1: {
                    convertView = inflater.inflate(R.layout.advanced_options_view_2, null);
                    EditText editText = (EditText) convertView.findViewById(R.id.map_location);
                    location = editText.getText().toString();
                    break;
                }
                case 2: {
                    convertView = inflater.inflate(R.layout.advanced_options_view_3, null);
                    CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.cb_permission);
                    if (checkBox.isChecked()) permission = 1;
                    break;
                }

            }
        }
            return convertView;

    }

}



