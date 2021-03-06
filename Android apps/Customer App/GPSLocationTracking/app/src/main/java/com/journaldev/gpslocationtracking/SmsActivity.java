package com.journaldev.gpslocationtracking;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.ListViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ListMenuItemView;
import android.telephony.SmsManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class SmsActivity extends Activity implements OnItemClickListener {

    private static SmsActivity inst;

    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;

    Button send;
    EditText code;
    ProgressBar spinner;
    boolean myMessage = true;
    private List<ChatBubble> ChatBubbles;
    private ArrayAdapter<ChatBubble> adapter;
    public static SmsActivity instance() {
        return inst;
    }

    public void onStart() {
        super.onStart();
        inst = this;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        smsListView = (ListView) findViewById(R.id.reyclerview_message_list);
        ChatBubbles = new ArrayList<>();
        adapter = new MessageAdapter(this, R.layout.left_chat_bubble, ChatBubbles);


        smsListView.setAdapter(adapter);
       spinner=(ProgressBar)findViewById(R.id.progressBar);
       spinner.setVisibility(View.VISIBLE);
       smsListView.setOnItemClickListener(this);



     //   refreshSmsInbox();
    }



    public void updateList(final String smsMessage) {

        if(smsMessage.contains("Unable"))
        {Toast.makeText(getApplicationContext(),smsMessage,Toast.LENGTH_LONG).show();
            spinner.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);}
        else if(smsMessage.contains("OYOB"))
        {spinner.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Intent i= new Intent(SmsActivity.this,BookingConf.class);
            i.putExtra("book_conf",smsMessage);
            String datein=getIntent().getExtras().getString("checkin");
            String dateout=getIntent().getExtras().getString("checkout");
            i.putExtra("guestno", getIntent().getExtras().getString("guestno"));
            i.putExtra("checkin",datein);
            i.putExtra("checkout",dateout);
            startActivity(i);
        }
        else{
            ChatBubble chbt = new ChatBubble(smsMessage, true);
            ChatBubbles.add(chbt);
        }
        if(!adapter.isEmpty())
        {spinner.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);}
        adapter.notifyDataSetChanged();
    }




    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        ChatBubble c=adapter.getItem(i);
        String item=c.getContent();



        if(item.contains("OYO_ID")){



        String[] temp=item.split("\n");
       final String[] x=temp[1].split(":");
       final String[] y=x[1].split("#");

            AlertDialog.Builder builder1 = new AlertDialog.Builder(SmsActivity.this);
            builder1.setMessage("Do you want to confirm your booking?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Book",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            SmsManager s=SmsManager.getDefault();
                            s.sendTextMessage(Variables.serverno, null, "OYOID" + y[0], null, null);
                            Toast.makeText(getApplicationContext(),"Thanks for booking.Wait for confirmation.",Toast.LENGTH_LONG).show();
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            spinner.setVisibility(View.VISIBLE);

                            dialog.cancel();
                        }
                    });

            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

    }}
}

