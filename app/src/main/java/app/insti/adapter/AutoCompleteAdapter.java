package app.insti.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.insti.utils.StringUtil;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Shivam Sharma on 13-08-2018.
 */


public class AutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private static final String TAG = AutoCompleteAdapter.class.getSimpleName();
    private Context mContext;
    private List<String> resultList = new ArrayList<String>();
    private List<Long> complaintIds = new ArrayList<>();

    public AutoCompleteAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return complaintIds.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) mContext
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.simple_dropdown_item_2line, parent, false);
//        }
//        ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position)); //Online Work
        return convertView;
    }

    @Override
    public Filter getFilter() {
//        Filter filter = new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults filterResults = new FilterResults();
//                if (constraint != null) {
//                    List<String> resultDropdown = findResultsForDropdown(mContext, constraint.toString());
//
//                    // Assign the data to the FilterResults
//                    filterResults.values = resultDropdown;
//                    filterResults.count = resultDropdown.size();
//                }
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                if (results != null && results.count > 0) {
//                    resultList = (List<String>) results.values;
//                    notifyDataSetChanged();
//                } else {
//                    notifyDataSetInvalidated();
//                }
//            }};
        return null;
    }

    /**
     * Returns a search result for the given book title.
     */
//    online work
//    private List<String> findResultsForDropdown(Context context, String stringEntered) {
//
//        if(!StringUtil.isBlank(stringEntered) && stringEntered.length()%3 == 0){
//
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//
//            OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(interceptor);
//
//            Retrofit.Builder builder =
//                    new Retrofit.Builder()
//                            .baseUrl(context.getResources().getString(R.string.base_server_url))
//                            .addConverterFactory(
//                                    GsonConverterFactory.create()
//                            );
//
//            Retrofit retrofit =
//                    builder
//                            .client(
//                                    httpClient.build()
//                            )
//                            .build();
//
//            ComplaintAPI client = retrofit.create(ComplaintAPI.class);
//            Call<List<Complaint.SuggestionSnippet>> call = client.getComplaintSuggestions(stringEntered);
//            try {
//                Response<List<Complaint.SuggestionSnippet>> listResponse = call.execute();
//                if(listResponse != null && listResponse.body() != null){
//
//                    List<String> complaints = new ArrayList<>();
//                    List<Long> complaintIds = new ArrayList<>();
//                    for(Complaint.SuggestionSnippet suggestionSnippet: listResponse.body()){
//
//                        String c = suggestionSnippet.getSummary();
//                        complaints.add(c);
//                        complaintIds.add(suggestionSnippet.getId());
//
//                    }
//
//                    this.complaintIds = complaintIds;
//                    this.resultList = complaints;
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//        return resultList;
//
//    }
}
