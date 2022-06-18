package app.institute.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import app.institute.R;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.Unit;


public class UnitsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private List<Unit> mItems;
    private Context context;
    private OnItemClickListener clickListener;
    private OnTaskCompleted listener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    public UnitsRecyclerAdapter(Context context, OnTaskCompleted listener, List<Unit> mItems)
    {

        super();

        this.mItems = mItems;
        this.context = context;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        if(i == TYPE_HEADER)
        {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_layout_help, viewGroup, false);
            return new VHeader(view);
        }

        else if(i == TYPE_ITEM)
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_units, viewGroup, false);
            return new ViewHolder(v);
        }

        throw new RuntimeException("there is no type that matches the type " + i + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i)
    {


        if (i == 0)
        {

            if (holder instanceof VHeader)
            {

                VHeader vHeader = (VHeader) holder;
                vHeader.button_test.setTag(i);
                vHeader.button_ask_expert.setTag(i);
            }
        }

        else if(holder instanceof ViewHolder)
        {

            ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.button_dpp.setTag(i-1);
            Unit unit = mItems.get(i-1);
            viewHolder.tv_topic_name.setText(unit.unit_name);
        }
    }


    class VHeader extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public Button button_test, button_ask_expert;

        public VHeader(View itemView)
        {

            super(itemView);


            button_test = (Button) itemView.findViewById(R.id.buttonTest);
            button_ask_expert = (Button) itemView.findViewById(R.id.buttonAskExpert);

            button_test.setOnClickListener(onButtonClickListener);
            button_ask_expert.setOnClickListener(onButtonClickListener);
        }



        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                if(v.getId() == R.id.buttonTest)
                {
                    listener.onTaskCompleted(true, (int) v.getTag(), "test");
                }

                else if(v.getId() == R.id.buttonAskExpert)
                {
                    listener.onTaskCompleted(true, (int) v.getTag(), "ask");
                }
            }
        };


        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public TextView tv_topic_name;
        public Button button_dpp;
        public RelativeLayout layout;

        public ViewHolder(View itemView)
        {

            super(itemView);

            tv_topic_name = (TextView)itemView.findViewById(R.id.tv_topic_name);
            layout = (RelativeLayout) itemView.findViewById(R.id.top_layout);
            button_dpp = (Button) itemView.findViewById(R.id.buttonDPP);

            button_dpp.setOnClickListener(onButtonClickListener);

            itemView.setOnClickListener(this);
        }


        private View.OnClickListener onButtonClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                if(v.getId() == R.id.buttonDPP)
                {

                    int i = (int) v.getTag();
                    listener.onTaskCompleted(true, (int) v.getTag(), "dpp");
                }
            }
        };

        @Override
        public void onClick(View v)
        {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }


    @Override
    public int getItemCount()
    {
        return mItems == null ? 0 : mItems.size() + 1;
    }


    @Override
    public int getItemViewType(int position)
    {

        if(position == 0)
        {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
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