package app.institute.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.institute.R;
import app.institute.helper.Helper;
import app.institute.model.Message;

import java.text.SimpleDateFormat;
import java.util.List;


public class InboxRecyclerAdapter extends RecyclerView.Adapter<InboxRecyclerAdapter.VersionViewHolder>
{

    private List<Message> list;

    Context context;
    OnItemClickListener clickListener;

    private String[] bgColors;


    public InboxRecyclerAdapter(Context context, List<Message> list)
    {
        this.context = context;
        this.list  = list;
        bgColors = context.getApplicationContext().getResources().getStringArray(R.array.background_color);
    }


    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_inbox, viewGroup, false);
        return new VersionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int i)
    {

        ShapeDrawable background = new ShapeDrawable();
        background.setShape(new OvalShape()); // or RoundRectShape()


        Message message = list.get(i);

        versionViewHolder.message_title.setText(Helper.toCamelCase(message.message_title));
        versionViewHolder.message_body.setText(message.message_body);
        versionViewHolder.thumbnail.setText(message.message_title.substring(0, 1).toUpperCase());

        try
        {
            String datetime = new SimpleDateFormat("hh:mm a").format(Long.parseLong(message.timestamp));
            versionViewHolder.timestamp.setText(datetime);
        }

        catch (Exception e)
        {

        }


        String color = bgColors[i % bgColors.length];
        background.getPaint().setColor(Color.parseColor(color));
        versionViewHolder.thumbnail.setBackground(background);
    }


    @Override
    public int getItemCount()
    {
        return list == null ? 0 : list.size();
    }


    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView thumbnail;
        TextView message_title;
        TextView message_body;
        TextView timestamp;


        public VersionViewHolder(View itemView)
        {

            super(itemView);

            thumbnail = (TextView) itemView.findViewById(R.id.thumbnail);
            message_title = (TextView) itemView.findViewById(R.id.message_title);
            message_body = (TextView) itemView.findViewById(R.id.message_body);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }


    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }


    public void SetOnItemClickListener(final OnItemClickListener itemClickListener)
    {
        this.clickListener = itemClickListener;
    }
}