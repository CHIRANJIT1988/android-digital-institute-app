package app.institute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.institute.R;
import app.institute.helper.Helper;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.DailyPracticePaper;


public class DailyPracticePapersRecyclerAdapter extends RecyclerView.Adapter<DailyPracticePapersRecyclerAdapter.ViewHolder>
{

    private List<DailyPracticePaper> mItems;
    private Context context;
    private OnItemClickListener clickListener;
    private OnTaskCompleted listener;


    public DailyPracticePapersRecyclerAdapter(Context context, OnTaskCompleted listener, List<DailyPracticePaper> mItems)
    {

        super();

        this.mItems = mItems;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_daily_practice_paper, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {

        DailyPracticePaper dpp = mItems.get(i);
        viewHolder.tv_paper_name.setText(Helper.toCamelCase(dpp.paper_name));
        viewHolder.tv_paper_date.setText(Helper.dateTimeFormat(dpp.paper_date));
    }


    @Override
    public int getItemCount()
    {
        return mItems.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public TextView tv_paper_name;
        public TextView tv_paper_date;

        public ViewHolder(View itemView)
        {

            super(itemView);

            tv_paper_name = (TextView)itemView.findViewById(R.id.tv_paper_name);
            tv_paper_date = (TextView)itemView.findViewById(R.id.tv_paper_date);

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