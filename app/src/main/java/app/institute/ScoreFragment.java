package app.institute;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.institute.model.MockTest;


public class ScoreFragment extends Fragment
{

    public ScoreFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_score, container, false);

        TextView score = (TextView) rootView.findViewById(R.id.my_score);
        ProgressBar progress = (ProgressBar) rootView.findViewById(R.id.progressBar2);

        score.setText(String.valueOf(MockTest.calculateScore()));

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}