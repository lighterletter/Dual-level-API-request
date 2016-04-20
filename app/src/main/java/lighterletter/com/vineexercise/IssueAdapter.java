package lighterletter.com.vineexercise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by john on 4/19/16.
 */
public class IssueAdapter extends BaseAdapter {
    ArrayList<String> userNames;
    ArrayList<String> titles;
    ArrayList<String> bodies;
    ArrayList<String> commentUrls;
    Context context;


    private static LayoutInflater inflater = null;

    public IssueAdapter(MainActivity mainActivity, ArrayList<String> userNames,
                        ArrayList<String> titles, ArrayList<String> bodies, ArrayList<String> commentUrls) {

        this.userNames = userNames;
        this.titles = titles;
        this.bodies = bodies;
        this.commentUrls = commentUrls;
        context = mainActivity;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userNames.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView userId;
        TextView title;
        TextView body;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.issues_item, null);
        holder.title = (TextView) rowView.findViewById(R.id.issue_title);
        holder.body = (TextView) rowView.findViewById(R.id.issue_body);
        holder.userId = (TextView) rowView.findViewById(R.id.issue_user);

        holder.title.setText(titles.get(position));
        holder.body.setText(bodies.get(position));
        holder.userId.setText(userNames.get(position));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //fetch comments
                CommentsBGTask commentTask = new CommentsBGTask(context);
                commentTask.execute(commentUrls.get(position));

            }
        });
        return rowView;
    }

}