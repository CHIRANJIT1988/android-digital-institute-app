package app.institute.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import app.institute.R;
import app.institute.helper.Blur;
import app.institute.model.MockTest;
import app.institute.model.Option;

import static app.institute.configuration.Configuration.DIAGRAM_URL;


public class TestResultRecyclerAdapter extends RecyclerView.Adapter<TestResultRecyclerAdapter.VersionViewHolder>
{

    private Context context = null;
    private OnItemClickListener clickListener;


    public TestResultRecyclerAdapter(Context context)
    {
        this.context = context;
    }


    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_test_result, viewGroup, false);
        return new VersionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final VersionViewHolder versionViewHolder, int i)
    {

        final MockTest mockTest = MockTest.testList.get(i);

        //versionViewHolder.question_number.setText(String.valueOf("Q" + mockTest.question.question_id));
        versionViewHolder.positive_marks.setText(String.valueOf("+" + mockTest.question.positive_marks));
        versionViewHolder.negative_marks.setText(String.valueOf("-" + mockTest.question.negative_marks));
        versionViewHolder.question.setText(mockTest.question.question);


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
                .load(DIAGRAM_URL + mockTest.test_id + "/" + mockTest.question.diagram) // thumbnail url goes here
                //.resize(70, 70)
                .transform(blurTransformation)
                .into(versionViewHolder.diagram, new Callback() {

                    @Override
                    public void onSuccess()
                    {

                        Picasso.with(context)
                                .load(DIAGRAM_URL + mockTest.test_id + "/" + mockTest.question.diagram) // image url goes here
                                //.resize(70, 70)
                                .placeholder(versionViewHolder.diagram.getDrawable())
                                .into(versionViewHolder.diagram);
                    }

                    @Override
                    public void onError() {

                    }
                });


        ShapeDrawable background = new ShapeDrawable();
        background.setShape(new OvalShape()); // or RoundRectShape()
        background.getPaint().setColor(Color.parseColor("#4CAF50"));
        versionViewHolder.positive_marks.setBackground(background);

        ShapeDrawable background1 = new ShapeDrawable();
        background1.setShape(new OvalShape()); // or RoundRectShape()
        background1.getPaint().setColor(Color.parseColor("#e53935"));
        versionViewHolder.negative_marks.setBackground(background1);


        for(Option m: mockTest.question.optionList)
        {

            if(m.is_correct == 1)
            {
                versionViewHolder.correct_answer.setText(m.option);
                break;
            }
        }


        if(mockTest.question.is_correct_answer == 0)
        {
            int imgResId = context.getResources().getIdentifier("ic_not_attempt", "drawable", "app.institute");
            versionViewHolder.thumbnail.setImageResource(imgResId);
        }

        else if(mockTest.question.is_correct_answer == 1)
        {
            int imgResId = context.getResources().getIdentifier("ic_correct", "drawable", "app.institute");
            versionViewHolder.thumbnail.setImageResource(imgResId);
        }

        else if(mockTest.question.is_correct_answer == -1)
        {
            int imgResId = context.getResources().getIdentifier("ic_wrong", "drawable", "app.institute");
            versionViewHolder.thumbnail.setImageResource(imgResId);
        }
    }


    @Override
    public int getItemCount()
    {
        return MockTest.testList == null ? 0 : MockTest.testList.size();
    }


    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView positive_marks;
        TextView negative_marks;
        TextView question;
        TextView correct_answer;
        ImageView thumbnail, diagram;


        public VersionViewHolder(View itemView)
        {

            super(itemView);

            positive_marks = (TextView) itemView.findViewById(R.id.positive_marks);
            negative_marks = (TextView) itemView.findViewById(R.id.negative_marks);
            question = (TextView) itemView.findViewById(R.id.question);
            correct_answer = (TextView) itemView.findViewById(R.id.correct_answer);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            diagram = (ImageView) itemView.findViewById(R.id.diagram);

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