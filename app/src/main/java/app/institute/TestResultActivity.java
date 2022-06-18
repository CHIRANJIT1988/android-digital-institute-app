package app.institute;

import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.institute.alert.CustomAlertDialog;
import app.institute.helper.OnAlertButtonClick;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.MockTest;
import app.institute.model.Question;
import app.institute.mysql.send.SyncMockTestScore;
import app.institute.session.SessionManager;


public class TestResultActivity extends AppCompatActivity implements OnTaskCompleted, OnAlertButtonClick
{

    private ViewPager mViewPager;
    private int count_correct, count_wrong, count_not_attempt;
    private int positive_score, negative_store;
    private int back_press_count = 0;
    private ProgressDialog pDialog;
    private MockTest m;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //assert getSupportActionBar() != null;
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setTitle("");


        this.pDialog = new ProgressDialog(this);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        // Fixes bug for disappearing fragment content
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        for(MockTest m: MockTest.testList)
        {

            if(m.question.is_correct_answer == 0)
            {
                count_not_attempt ++;
            }

            else if(m.question.is_correct_answer == 1)
            {
                count_correct ++;
                positive_score += m.question.positive_marks;
            }

            else if(m.question.is_correct_answer == -1)
            {
                count_wrong++;
                negative_store += m.question.negative_marks;
            }
        }


        this.m = new MockTest(getIntent().getIntExtra("TEST_ID", 0), count_correct, count_wrong, count_not_attempt, new Question(positive_score, negative_store));
        new SyncMockTestScore(getApplicationContext(), this).sync(m, new SessionManager(this).getUserId());
        initProgressDialog("Score Submitting. Please Wait ...");
    }


    @Override
    public void onBackPressed()
    {

        if(back_press_count == 0)
        {
            back_press_count++;
            Toast.makeText(getApplicationContext(), "Press Back Button again to Exit", Toast.LENGTH_LONG).show();
        }

        else
        {
            finish();
        }
    }


    private void initProgressDialog(String message)
    {

        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(false);
        pDialog.show();
    }


    private void setupViewPager(ViewPager viewPager)
    {

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new ScoreFragment(), "YOUR SCORE");
        adapter.addFrag(new ResultFragment(this), "SUMMERY");

        viewPager.setAdapter(adapter);
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }


        public void addFrag(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onAlertButtonClick(boolean flag, int code)
    {

        if(flag && code == 200)
        {
            new SyncMockTestScore(getApplicationContext(), this).sync(m, new SessionManager(this).getUserId());
            initProgressDialog("Score Submitting. Please Wait ...");
        }
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            if(!flag)
            {
                new CustomAlertDialog(this, TestResultActivity.this).showOKDialog("Fail", "Failed to Submit Score", "TRY AGAIN");
            }
        }

        catch (Exception e)
        {

        }

        finally
        {

            if(pDialog.isShowing())
            {
                pDialog.dismiss();
            }

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}