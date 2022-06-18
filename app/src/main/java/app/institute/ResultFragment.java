package app.institute;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.institute.adapter.TestResultRecyclerAdapter;


public class ResultFragment extends Fragment
{

    private TestResultRecyclerAdapter simpleRecyclerAdapter;
    private Context context;


    public ResultFragment()
    {

    }


    @SuppressLint("ValidFragment")
    public ResultFragment(Context context)
    {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_result, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);


        if (simpleRecyclerAdapter == null)
        {
            simpleRecyclerAdapter = new TestResultRecyclerAdapter(context);
            recyclerView.setAdapter(simpleRecyclerAdapter);
        }


        simpleRecyclerAdapter.SetOnItemClickListener(new TestResultRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {

            }
        });


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