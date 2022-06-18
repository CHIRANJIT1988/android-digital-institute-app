package app.institute;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.institute.adapter.TestPapersRecyclerAdapter;
import app.institute.alert.CustomAlertDialog;
import app.institute.helper.OnAlertButtonClick;
import app.institute.helper.OnCountCompleted;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.Branch;
import app.institute.model.MockTest;
import app.institute.mysql.receive.CountMockTestPapers;
import app.institute.mysql.receive.ReceiveTestPapers;
import app.institute.session.SessionManager;


public class TestPaperActivity extends AppCompatActivity implements OnTaskCompleted, OnAlertButtonClick, OnCountCompleted
{

    private TestPapersRecyclerAdapter mAdapter;
    private ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_paper);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("PHYSICS");

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(TestPaperActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);


        final Branch _branch = (Branch) getIntent().getSerializableExtra("BRANCH");


        progress = (ProgressBar) findViewById(R.id.pbLoading);
        progress.setVisibility(View.VISIBLE);
        MockTest.testPaperList.clear();

        new ReceiveTestPapers(getApplicationContext(), this).receive(_branch, new SessionManager(this).getUserId());
        new CountMockTestPapers(getApplicationContext(), this).count(_branch);


        mAdapter = new TestPapersRecyclerAdapter(getApplicationContext(), this, MockTest.testPaperList);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.SetOnItemClickListener(new TestPapersRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {


                if(MockTest.testPaperList.get(position).attempted_on.isEmpty())
                {

                    Intent intent = new Intent(TestPaperActivity.this, QuizActivity.class);
                    intent.putExtra("TEST_ID", MockTest.testPaperList.get(position).test_id);
                    startActivity(intent);

                    finish();
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "You have already attempted this mock test", Toast.LENGTH_LONG).show();
                }
            }
        });


        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_in);
        final RelativeLayout mock_test_layout = (RelativeLayout) findViewById(R.id.mock_test_layout);
        mock_test_layout.startAnimation(animation);
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

            if(code == 200 && message.equals("total-mock-test"))
            {
                TextView total_mock_test = (TextView) findViewById(R.id.total_mock_test);
                total_mock_test.setText(String.valueOf(count));
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

                if(message.equals("test"))
                {
                    startActivity(new Intent(TestPaperActivity.this, QuizActivity.class));
                }

                else if(message.equals("read"))
                {
                    startActivity(new Intent(TestPaperActivity.this, OnlinePDFViewerActivity.class));
                }
            }

            else
            {
                new CustomAlertDialog(TestPaperActivity.this, this).showOKDialog("", message, "CLOSE");
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