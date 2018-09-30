package app.insti.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.insti.R;
import app.insti.api.model.AboutIndividual;
import app.insti.interfaces.ItemClickListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class AboutIndividualAdapter extends BaseAdapter {
    private List<AboutIndividual> individuals;
    private Context context;

    public AboutIndividualAdapter(List<AboutIndividual> individuals) {
        this.individuals = individuals;
    }

    @Override
    public int getCount() {
        return individuals.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        ViewHolder holder = new ViewHolder();
        View rowView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.about_individual_card, null);

        holder.pictureImageView = rowView.findViewById(R.id.picture_about);
        holder.nameTextView = rowView.findViewById(R.id.name_individual_about);

        Picasso.get().load("https://insti.app/team-pics/" + individuals.get(position).getImageName()).resize(0, 300).into(holder.pictureImageView);
        holder.nameTextView.setText(individuals.get(position).getName());

        return rowView;
    }

    public class ViewHolder {
        CircleImageView pictureImageView;
        TextView nameTextView;
    }
}
