package ru.loredan13.btchat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: loredan
 * Date: 05.08.13
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public class ChatActivity extends Activity {
    Handler handler;
    LinearLayout history;
    ChatThread chat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("Chat", "Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        ((EditText) findViewById(R.id.et_msg)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMsg(v);
                    return true;
                }
                return false;
            }
        });

        history = (LinearLayout) findViewById(R.id.ll_history);
        Log.i("Chat", "History locked");
        Log.d("history", history == null ? "null" : "OK");
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.i("Chat", "Message handler");
                TextView message = (TextView) LayoutInflater.from(ChatActivity.this)
                        .inflate(R.layout.message, history, false);
                message.setText(new String((byte[]) msg.obj, 0, msg.arg1));
                message.setGravity(Gravity.LEFT);
                history.addView(message);
                Log.i("Chat", "Message handled");
            }
        };
        Log.i("Chat", "Start chat thread");
        chat = new ChatThread(handler);
        chat.start();
        Log.i("Chat", "Thread started");
    }

    public void sendMsg(View view) {
        Log.i("Chat", "Sending msg");
        EditText msg = (EditText) findViewById(R.id.et_msg);
        TextView message = (TextView) LayoutInflater.from(this)
                .inflate(R.layout.message, history, false);
        message.setText(msg.getText().toString());
        message.setGravity(Gravity.RIGHT);
        history.addView(message);
        chat.write(msg.getText().toString());
        msg.setText("");
        Log.i("Chat", "Message sent");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chat.close();
    }
}
