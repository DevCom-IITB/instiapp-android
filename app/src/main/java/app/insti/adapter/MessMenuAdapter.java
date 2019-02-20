package app.insti.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.insti.R;
import app.insti.api.model.MessMenu;

public class MessMenuAdapter extends RecyclerView.Adapter<MessMenuAdapter.ViewHolder> {
    private List<MessMenu> messMenus;

    public MessMenuAdapter(List<MessMenu> messMenus) {
        this.messMenus = messMenus;
        this.setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.mess_menu_card, parent, false);

        final ViewHolder postViewHolder = new ViewHolder(postView);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessMenuAdapter.ViewHolder holder, int position) {
        MessMenu messMenu = messMenus.get(position);
        holder.day.setText(generateDayString(messMenu.getDay()));
        holder.breakfast.setText(messMenu.getBreakfast());
        holder.lunch.setText(messMenu.getLunch());
        holder.snacks.setText(messMenu.getSnacks());
        holder.dinner.setText(messMenu.getDinner());
    }

    @Override
    public long getItemId(int position) {
        return messMenus.get(position).getMealID().hashCode();
    }

    @Override
    public int getItemCount() {
        return messMenus.size();
    }

    public void setMenu(List<MessMenu> menus) {
        messMenus = menus;
    }

    private String generateDayString(int day) {
        switch (day) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                throw new IndexOutOfBoundsException("DayIndexOutOfBounds: " + day);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView day;
        private TextView breakfast;
        private TextView lunch;
        private TextView snacks;
        private TextView dinner;

        public ViewHolder(View itemView) {
            super(itemView);

            day = itemView.findViewById(R.id.day_text_view);
            breakfast = itemView.findViewById(R.id.breakfast_text_view);
            lunch = itemView.findViewById(R.id.lunch_text_view);
            snacks = itemView.findViewById(R.id.snacks_text_view);
            dinner = itemView.findViewById(R.id.dinner_text_view);
        }
    }
}
