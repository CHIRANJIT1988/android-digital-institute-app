package app.institute.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import app.institute.helper.Blur;

import java.util.List;

import app.institute.R;
import app.institute.model.Subject;

import static app.institute.configuration.Configuration.IMAGE_URL;


public class DashboardRecyclerAdapter extends RecyclerView.Adapter<DashboardRecyclerAdapter.ViewHolder>
{

    private List<Subject> mItems;
    private OnItemClickListener clickListener;
    private Context context;


    public DashboardRecyclerAdapter(Context context, List<Subject> mItems)
    {

        super();

        this.context = context;
        this.mItems = mItems;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item_home, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i)
    {

        final Subject subject = mItems.get(i);

        viewHolder.tvSubject.setText(subject.subject_name.toUpperCase());

        Transformation blurTransformation = new Transformation() {

            @Override
            public Bitmap transform(Bitmap source) {
                Bitmap blurred = Blur.fastblur(context, source, 10);
                source.recycle();
                return blurred;
            }

            @Override
            public String key() {
                return "blur()";
            }
        };

        Picasso.with(context)
                .load(IMAGE_URL + subject.subject_name.toLowerCase().replace(" ", "-") + ".png") // thumbnail url goes here
                .resize(70, 70)
                .transform(blurTransformation)
                .into(viewHolder.thumbnail, new Callback() {

                    @Override
                    public void onSuccess()
                    {

                        Picasso.with(context)
                                .load(IMAGE_URL + subject.subject_name.toLowerCase().replace(" ", "-") + ".png") // image url goes here
                                .resize(70, 70)
                                .placeholder(viewHolder.thumbnail.getDrawable())
                                .into(viewHolder.thumbnail);
                    }

                    @Override
                    public void onError() {

                    }
                });

        //int imgResId = context.getResources().getIdentifier(subject.subject_name.toLowerCase(), "drawable", "app.institute");
        //viewHolder.thumbnail.setImageResource(imgResId);
    }


    @Override
    public int getItemCount()
    {
        return mItems.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public TextView tvSubject;
        public ImageView thumbnail;

        public ViewHolder(View itemView)
        {

            super(itemView);

            tvSubject = (TextView)itemView.findViewById(R.id.tvSubject);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

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