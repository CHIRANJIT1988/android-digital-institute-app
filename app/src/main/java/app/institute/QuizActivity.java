package app.institute;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import app.institute.alert.CustomAlertDialog;
import app.institute.helper.Blur;
import app.institute.helper.OnAlertButtonClick;
import app.institute.helper.OnTaskCompleted;
import app.institute.model.MockTest;
import app.institute.mysql.receive.ReceiveTest;

import static app.institute.configuration.Configuration.DIAGRAM_URL;


public class QuizActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleted, OnAlertButtonClick
{

    private Chronometer myChronometer;
    private Button button_previous, button_next, button_end;
    private ViewPager viewPager;
    private TextView tv_question_number, tv_positive_marks, tv_negative_marks;

    private static QuizActivity activity;
    private static int currentPage = 0, test_id;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        activity = QuizActivity.this;

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //assert getSupportActionBar() != null;
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setTitle("Quiz");


        findViewById();
        setOnClickListener();
        set_marks_background();


        test_id = getIntent().getIntExtra("TEST_ID", 0);
        MockTest.testList.clear();
        new ReceiveTest(getApplicationContext(), this).receive(test_id);
    }


    private void set_marks_background()
    {

        ShapeDrawable background = new ShapeDrawable();
        background.setShape(new OvalShape()); // or RoundRectShape()
        background.getPaint().setColor(Color.parseColor("#4CAF50"));
        tv_positive_marks.setBackground(background);

        ShapeDrawable background1 = new ShapeDrawable();
        background1.setShape(new OvalShape()); // or RoundRectShape()
        background1.getPaint().setColor(Color.parseColor("#e53935"));
        tv_negative_marks.setBackground(background1);
    }


    private void timer(final int duration)
    {

        myChronometer.setBase(SystemClock.elapsedRealtime());
        myChronometer.start();

        myChronometer.setOnChronometerTickListener(

                new Chronometer.OnChronometerTickListener(){

                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        // TODO Auto-generated method stub
                        //long myElapsedMillis = SystemClock.elapsedRealtime() - myChronometer.getBase();
                        //String strElapsedMillis = "Elapsed milliseconds: " + myElapsedMillis;
                        //Toast.makeText(getApplicationContext(), strElapsedMillis, Toast.LENGTH_SHORT).show();


                        int elapsed_time = Integer.parseInt(chronometer.getText().toString().split(":")[0]);

                        if(duration == elapsed_time)
                        {
                            myChronometer.stop();
                            new CustomAlertDialog(activity, QuizActivity.this).showOKDialog("Time Expired", "Exam time expired", "END NOW");
                        }
                    }}
        );
    }


    private void findViewById()
    {

        viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        tv_question_number = (TextView) findViewById(R.id.question_number);
        tv_positive_marks = (TextView) findViewById(R.id.positive_marks);
        tv_negative_marks = (TextView) findViewById(R.id.negative_marks);

        myChronometer = (Chronometer)findViewById(R.id.chronometer);

        button_previous = (Button) findViewById(R.id.btnPrevious);
        button_next = (Button) findViewById(R.id.btnNext);
        button_end = (Button) findViewById(R.id.btnEnd);
    }


    private void setOnClickListener()
    {

        button_next.setOnClickListener(this);
        button_previous.setOnClickListener(this);
        button_end.setOnClickListener(this);
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.btnPrevious:

                if(currentPage != 0)
                {
                    currentPage--;
                }

                break;

            case R.id.btnNext:

                if (currentPage != MockTest.testList.size()-1)
                {
                    currentPage ++;
                }

                break;

            case R.id.btnEnd:

                new CustomAlertDialog(QuizActivity.this, this).showConfirmationDialog("End Test ?", "Are you sure want to end now ?", "END NOW", "NO");
                break;
        }

        viewPager.setCurrentItem(currentPage, true);
        tv_question_number.setText(String.valueOf("Q No ~ " + (currentPage + 1) + " / " + MockTest.testList.size()));
        tv_positive_marks.setText(String.valueOf("+" + MockTest.testList.get(currentPage).question.positive_marks));
        tv_negative_marks.setText(String.valueOf("-" + MockTest.testList.get(currentPage).question.negative_marks));
    }


    private void setupViewPager(ViewPager viewPager, List<MockTest> mockTestList)
    {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (MockTest mockTest: mockTestList)
        {
            adapter.addFrag(new QuestionFragment(mockTest, this), "");
        }

        viewPager.setAdapter(adapter);
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter
    {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
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


    public static class QuestionFragment extends Fragment
    {

        private MockTest mockTestObj;
        //private List<RadioButton> radioList = new ArrayList<>();
        private Context context;

        private RadioGroup radioGroup;


        public QuestionFragment()
        {

        }


        @SuppressLint("ValidFragment")
        public QuestionFragment(MockTest mockTestObj, Context context)
        {
            this.context = context;
            this.mockTestObj = mockTestObj;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {

            View view = inflater.inflate(R.layout.fragment_question, container, false);
            radioGroup = (RadioGroup) view.findViewById(R.id.radio_selection_group);

            TextView tv_question = (TextView) view.findViewById(R.id.question);
            tv_question.setText(Html.fromHtml(mockTestObj.question.question));

            final ImageView iv_diagram = (ImageView) view.findViewById(R.id.diagram);


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
                    .load(DIAGRAM_URL + mockTestObj.test_id + "/" + mockTestObj.question.diagram) // thumbnail url goes here
                    //.resize(70, 70)
                    .transform(blurTransformation)
                    .into(iv_diagram, new Callback() {

                        @Override
                        public void onSuccess()
                        {

                            Picasso.with(context)
                                    .load(DIAGRAM_URL + mockTestObj.test_id + "/" + mockTestObj.question.diagram) // image url goes here
                                    //.resize(70, 70)
                                    .placeholder(iv_diagram.getDrawable())
                                    .into(iv_diagram);
                        }

                        @Override
                        public void onError() {

                        }
                    });


            addOptions();
            return view;
        }


        private void addOptions()
        {

            for (int i = 0; i < mockTestObj.question.optionList.size(); i++)
            {

                //instantiate...
                RadioButton radioButton = (RadioButton) getActivity().getLayoutInflater().inflate(R.layout.question_layout, null);
                radioButton.setPadding(5, 5, 5, 5);

                //set the values that you would otherwise hardcode in the xml...
                //radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));

                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(0, 10, 0, 10);

                radioButton.setLayoutParams(layoutParams);

                //label the button...
                radioButton.setOnClickListener(activity);

                radioButton.setText(Html.fromHtml(mockTestObj.question.optionList.get(i).option));
                radioButton.setId(i);

                //add to list
                //radioList.add(radioButton);
                //add it to the group.
                radioGroup.addView(radioButton, i);


                radioButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view)
                    {

                        ((RadioGroup) view.getParent()).check(view.getId());

                        int is_correct = mockTestObj.question.optionList.get(view.getId()).is_correct;

                        if(is_correct == 1)
                        {

                            mockTestObj.question.is_correct_answer = 1;
                            //Toast.makeText(context, "Correct Answer", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            mockTestObj.question.is_correct_answer = -1;
                            //Toast.makeText(context, "Wrong Answer", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }


    @Override
    public void onAlertButtonClick(boolean flag, int code)
    {

        if(flag && code == 200)
        {

            Intent intent = new Intent(QuizActivity.this, TestResultActivity.class);
            intent.putExtra("TEST_ID", test_id);
            startActivity(intent);

            finish();
        }
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        if(flag && code == 200)
        {

            LinearLayout footer_layout = (LinearLayout) findViewById(R.id.footer_quiz);
            footer_layout.setVisibility(View.VISIBLE);

            ProgressBar progress = (ProgressBar) findViewById(R.id.pbLoading);
            progress.setVisibility(View.GONE);


            viewPager.setOffscreenPageLimit(MockTest.testList.size());
            setupViewPager(viewPager, MockTest.testList);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position)
                {

                    currentPage = position;

                    tv_question_number.setText(String.valueOf("Q No ~ " + (currentPage + 1) + " / " + MockTest.testList.size()));
                    tv_positive_marks.setText(String.valueOf("+" + MockTest.testList.get(currentPage).question.positive_marks));
                    tv_negative_marks.setText(String.valueOf("+" + MockTest.testList.get(currentPage).question.negative_marks));
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            timer(MockTest.testList.get(currentPage).duration);
            tv_question_number.setText(String.valueOf("Q No ~ " + (currentPage + 1) + " / " + MockTest.testList.size()));
            tv_positive_marks.setText(String.valueOf("+" + MockTest.testList.get(currentPage).question.positive_marks));
            tv_negative_marks.setText(String.valueOf("-" + MockTest.testList.get(currentPage).question.negative_marks));
        }
    }


    @Override
    public void onBackPressed()
    {

        if(MockTest.testList.size() != 0)
        {
            new CustomAlertDialog(QuizActivity.this, this).showConfirmationDialog("End Test ?", "Are you sure want to end now ?", "END NOW", "NO");
        }

        else
        {
            finish();
        }

        /*for(int i=0; i<MockTest.testList.size(); i++)
        {

            if(MockTest.testList.get(i).question.is_correct_answer == 1)
            {
                Toast.makeText(getApplicationContext(), MockTest.testList.get(i).question.question_id + " > " + "Correct", Toast.LENGTH_SHORT).show();
            }

            else
            {
                Toast.makeText(getApplicationContext(), MockTest.testList.get(i).question.question_id + " > " + "Wrong", Toast.LENGTH_SHORT).show();
            }
        }*/
    }
}