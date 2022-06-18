package app.institute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import app.institute.R;
import app.institute.helper.Helper;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.MockTest;


public class TestPapersRecyclerAdapter extends RecyclerView.Adapter<TestPapersRecyclerAdapter.ViewHolder>
{

    private List<MockTest> mItems;
    private Context context;
    private OnItemClickListener clickListener;
    private OnTaskCompleted listener;


    public TestPapersRecyclerAdapter(Context context, OnTaskCompleted listener, List<MockTest> mItems)
    {

        super();

        this.mItems = mItems;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_test_paper, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {

        MockTest mockTest = mItems.get(i);
        viewHolder.tv_test_name.setText(Helper.toCamelCase(mockTest.test_name));
        viewHolder.tv_total_marks.setText(String.valueOf("Total Marks ~ " + mockTest.total_marks));
        viewHolder.tv_duration.setText(String.valueOf("Duration ~ " + mockTest.total_marks + " Minutes"));

        viewHolder.tv_progress.setText(String.valueOf(mockTest.percentage + "%"));
        viewHolder.progress_bar.setProgress(mockTest.percentage);


        if(mockTest.attempted_on.isEmpty())
        {
            viewHolder.tv_attempted_on.setText("START NOW");
        }

        else
        {
            viewHolder.tv_attempted_on.setText(String.valueOf("Attempted On ~ " + Helper.dateTimeFormat(mockTest.attempted_on)));
        }
    }


    @Override
    public int getItemCount()
    {
        return mItems.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public TextView tv_test_name, tv_total_marks, tv_duration, tv_attempted_on, tv_progress;
        public ProgressBar progress_bar;


        public ViewHolder(View itemView)
        {

            super(itemView);

            tv_test_name = (TextView)itemView.findViewById(R.id.tv_test_name);
            tv_total_marks = (TextView)itemView.findViewById(R.id.total_marks);
            tv_duration = (TextView)itemView.findViewById(R.id.duration);
            tv_attempted_on = (TextView)itemView.findViewById(R.id.attempted_on);
            tv_progress = (TextView)itemView.findViewById(R.id.tv_progress);
            progress_bar = (ProgressBar) itemView.findViewById(R.id.progress_bar);

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