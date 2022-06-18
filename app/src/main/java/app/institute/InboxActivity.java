package app.institute;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import org.json.JSONException;
import org.json.JSONObject;

import app.institute.adapter.InboxRecyclerAdapter;
import app.institute.model.Message;
import app.institute.sqlite.SQLiteDatabaseHelper;

import java.util.List;

import static app.institute.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static app.institute.CommonUtilities.EXTRA_MESSAGE;


public class InboxActivity extends AppCompatActivity
{

    private RecyclerView recyclerView;
    private InboxRecyclerAdapter adapter;
    private List<Message> list;

    private boolean first_load = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Notifications");


        recyclerView = (RecyclerView) findViewById(R.id.list);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        list = new SQLiteDatabaseHelper(this).getAllMessage();


        if (adapter == null)
        {
            adapter = new InboxRecyclerAdapter(this, list);
            recyclerView.setAdapter(adapter);
        }


        adapter.SetOnItemClickListener(new InboxRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

            }
        });


        swipeDelete();
        first_load = false;


        this.registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

        //Mark as Read
        new SQLiteDatabaseHelper(this).setAsRead();


        // Instantiate NotificationManager Class
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Cancel notification
        mNotificationManager.cancel(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_clear_all, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:

                finish();
                return true;

            case R.id.action_clear:

                new SQLiteDatabaseHelper(this).deleteAllRow(SQLiteDatabaseHelper.TABLE_INBOX);
                list.clear();
                adapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy()
    {

        try
        {
            unregisterReceiver(mHandleMessageReceiver);
        }

        catch (Exception e)
        {
            Log.e("UnRegister Error", "> " + e.getMessage());
        }

        super.onDestroy();
    }


    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent) {

            try
            {


                String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
                /**
                 * Take appropriate action on this message
                 * depending upon your app requirement
                 * For now i am just displaying it on the screen
                 * */


                // Showing received message
                // lblMessage.append(newMessage + "\n");


                if(newMessage == null)
                {
                    return;
                }


                Log.v("message: ", newMessage);

                try
                {

                    JSONObject jsonObject = new JSONObject(newMessage);

                    String message_id = jsonObject.getString("message_id");
                    String message_title = jsonObject.getString("message_title");
                    String message_body = jsonObject.getString("message_body");

                    list.add(new Message(message_id, message_title, message_body, String.valueOf(System.currentTimeMillis()), 1));
                }

                catch (JSONException e)
                {

                }

                finally
                {
                    adapter.notifyDataSetChanged();
                }
            }

            catch(Exception e)
            {

            }
        }
    };


    private void swipeDelete()
    {

        SwipeableRecyclerViewTouchListener swipeTouchListener =

                new SwipeableRecyclerViewTouchListener(recyclerView,

                        new SwipeableRecyclerViewTouchListener.SwipeListener() {

                            @Override
                            public boolean canSwipe(int position)
                            {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {

                                if(!first_load)
                                {

                                    for (int position : reverseSortedPositions)
                                    {
                                        new SQLiteDatabaseHelper(getApplicationContext()).deleteMessage(list.get(position).message_id);
                                        list.remove(position);
                                        adapter.notifyItemRemoved(position);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {

                                if(!first_load)
                                {

                                    for (int position : reverseSortedPositions)
                                    {
                                        new SQLiteDatabaseHelper(getApplicationContext()).deleteMessage(list.get(position).message_id);
                                        list.remove(position);
                                        adapter.notifyItemRemoved(position);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });

        recyclerView.addOnItemTouchListener(swipeTouchListener);
    }
}