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
public class CommentAdapter extends BaseAdapter {
    private ArrayList<String> commentNames = new ArrayList<String>();
    private ArrayList<String> commentBodies = new ArrayList<String>();
    Context context;

    private static LayoutInflater inflater = null;

    public CommentAdapter(Context context, ArrayList<String> commentNames, ArrayList<String> commentBodies) {

        this.commentNames = commentNames;
        this.commentBodies = commentBodies;
        this.context = context;

        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return commentNames.size();
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
        TextView body;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.comments_item, null);

        holder.body = (TextView) rowView.findViewById(R.id.comment_body);
        holder.userId = (TextView) rowView.findViewById(R.id.comment_user);

        holder.body.setText(commentBodies.get(position));
        holder.userId.setText(commentNames.get(position));

        return rowView;
    }
}
