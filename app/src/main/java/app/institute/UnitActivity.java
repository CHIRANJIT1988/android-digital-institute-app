package app.institute;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import app.institute.adapter.UnitsRecyclerAdapter;
import app.institute.alert.CustomAlertDialog;
import app.institute.helper.Blur;
import app.institute.helper.OnAlertButtonClick;
import app.institute.helper.OnCountCompleted;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.Branch;
import app.institute.model.Unit;
import app.institute.mysql.receive.CountDPP;
import app.institute.mysql.receive.CountUnit;
import app.institute.mysql.receive.ReceiveUnits;

import static app.institute.configuration.Configuration.IMAGE_URL;


public class UnitActivity extends AppCompatActivity implements OnTaskCompleted, OnAlertButtonClick, OnCountCompleted
{

    private UnitsRecyclerAdapter mAdapter;
    private ProgressBar progress;
    private ImageView thumbnail;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final Branch _branch = (Branch) getIntent().getSerializableExtra("BRANCH");

        setTitle(_branch._subject.subject_name.toUpperCase());

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(UnitActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);


        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        progress = (ProgressBar) findViewById(R.id.pbLoading);
        progress.setVisibility(View.VISIBLE);

        Unit.unitList.clear();
        new ReceiveUnits(getApplicationContext(), this).receive(_branch);
        new CountUnit(getApplicationContext(), this).count(_branch);
        new CountDPP(getApplicationContext(), this).count(_branch);


        mAdapter = new UnitsRecyclerAdapter(getApplicationContext(), this, Unit.unitList);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.SetOnItemClickListener(new UnitsRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                Unit unit = Unit.unitList.get(position-1);

                Intent intent = new Intent(UnitActivity.this, TopicActivity.class);
                intent.putExtra("UNIT", new Unit(_branch, unit.unit_id, unit.unit_name));
                startActivity(intent);
            }
        });


        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_in);

        final RelativeLayout unit_layout = (RelativeLayout) findViewById(R.id.unit_layout);
        final RelativeLayout dpp_layout = (RelativeLayout) findViewById(R.id.dpp_layout);

        unit_layout.startAnimation(animation);
        dpp_layout.startAnimation(animation);


        Transformation blurTransformation = new Transformation() {

            @Override
            public Bitmap transform(Bitmap source) {
                Bitmap blurred = Blur.fastblur(UnitActivity.this, source, 10);
                source.recycle();
                return blurred;
            }

            @Override
            public String key() {
                return "blur()";
            }
        };

        Picasso.with(UnitActivity.this)
                .load(IMAGE_URL + _branch._subject.subject_name.toLowerCase() + ".png") // thumbnail url goes here
                .resize(70, 70)
                .transform(blurTransformation)
                .into(thumbnail, new Callback() {

                    @Override
                    public void onSuccess()
                    {

                        Picasso.with(UnitActivity.this)
                                .load(IMAGE_URL + _branch._subject.subject_name.toLowerCase() + ".png") // image url goes here
                                .resize(100, 100)
                                .placeholder(thumbnail.getDrawable())
                                .into(thumbnail);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }


    @Override
    public void onAlertButtonClick(boolean flag, int code)
    {

        if (flag && code == 200)
        {
            finish();
        }
    }


    @Override
    public void onCountCompleted(int code, int count, String message)
    {

        try
        {

            if(code == 200 && message.equals("unit"))
            {
                TextView total_unit = (TextView) findViewById(R.id.total_unit);
                total_unit.setText(String.valueOf(count));
            }

            if(code == 200 && message.equals("dpp"))
            {
                TextView total_dpp = (TextView) findViewById(R.id.total_dpp);
                total_dpp.setText(String.valueOf(count));
            }
        }

        catch (Exception e)
        {

        }
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {


        try
        {

            if (flag && code == 200)
            {
                mAdapter.notifyDataSetChanged();
            }

            else if(flag)
            {

                final Branch branch = (Branch) getIntent().getSerializableExtra("BRANCH");

                if(message.equals("test"))
                {

                    Intent intent = new Intent(UnitActivity.this, TestPaperActivity.class);
                    intent.putExtra("BRANCH", branch);
                    startActivity(intent);

                    finish();
                }

                else if(message.equals("dpp"))
                {

                    Intent intent = new Intent(UnitActivity.this, DailyPracticePaperActivity.class);
                    intent.putExtra("UNIT", new Unit(branch, Unit.unitList.get(code).unit_id));
                    startActivity(intent);


                    /*if(!Unit.unitList.get(code).daily_practice_paper.equals(""))
                    {
                        Intent intent = new Intent(UnitActivity.this, OnlinePDFViewerActivity.class);
                        intent.putExtra("FILE_NAME", "dpp/" + Unit.unitList.get(code).daily_practice_paper);
                        startActivity(intent);
                    }

                    else
                    {
                        Toast.makeText(getApplicationContext(), "DPP Coming Soon ...", Toast.LENGTH_LONG).show();
                    }*/
                }

                else if(message.equals("ask"))
                {
                    Intent intent = new Intent(UnitActivity.this, EmailComposeActivity.class);
                    intent.putExtra("BRANCH", branch);
                    startActivity(intent);
                }
            }

            else
            {
                new CustomAlertDialog(UnitActivity.this, this).showOKDialog("Sorry", message, "CLOSE");
            }
        }

        catch (Exception e)
        {

        }

        finally
        {
            progress.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:
            {
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}