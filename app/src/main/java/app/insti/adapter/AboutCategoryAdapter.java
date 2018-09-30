package app.insti.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import app.insti.R;
import app.insti.activity.MainActivity;
import app.insti.api.model.AboutCategory;
import app.insti.api.model.AboutIndividual;
import app.insti.view.ScrollResistantGridView;

public class AboutCategoryAdapter extends RecyclerView.Adapter<AboutCategoryAdapter.ViewHolder> {
    private List<AboutCategory> categories;
    private Context context;

    public AboutCategoryAdapter(List<AboutCategory> categories, MainActivity mainActivity) {
        this.categories = categories;
        this.context = mainActivity;
    }

    @NonNull
    @Override
    public AboutCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.about_category_card, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutCategoryAdapter.ViewHolder holder, int position) {
        final AboutCategory category = categories.get(position);
        holder.nameTextView.setText(category.getName());

        final List<AboutIndividual> individuals = category.getIndividuals();
        AboutIndividualAdapter individualAdapter = new AboutIndividualAdapter(individuals);
        holder.individualsGridView.setAdapter(individualAdapter);
        holder.individualsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String individualId = individuals.get(position).getId();
                if (individualId == null) {
                    return;
                }
                /*Repository Links*/
                if (individualId.startsWith("https://github.com/")) {
                    openWebURL(individualId);
                    return;
                }
                ((MainActivity) context).openUserFragment(individualId);
            }
        });
    }

    private void openWebURL(String URL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        context.startActivity(browse);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ScrollResistantGridView individualsGridView;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.name_category_about);
            individualsGridView = itemView.findViewById(R.id.grid_view_about);
        }
    }
}
