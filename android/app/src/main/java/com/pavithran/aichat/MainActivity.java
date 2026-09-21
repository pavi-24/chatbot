package com.pavithran.aichat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private ChatAdapter adapter;
    private EditText input;
    private TextView status;
    private UUID conversationId;
    private String deviceId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("pavi_ai", Context.MODE_PRIVATE);
        deviceId = prefs.getString("device_id", null);
        if (deviceId == null) {
            deviceId = "android-" + UUID.randomUUID();
            prefs.edit().putString("device_id", deviceId).apply();
        }

        RecyclerView list = findViewById(R.id.chatList);
        adapter = new ChatAdapter();
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        input = findViewById(R.id.messageInput);
        status = findViewById(R.id.status);
        Button send = findViewById(R.id.sendButton);
        Button newChat = findViewById(R.id.newChat);

        adapter.add(new ChatMessage("assistant", "Hey! I'm Pavi AI 👋\nAsk me anything."));
        send.setOnClickListener(v -> sendMessage());
        newChat.setOnClickListener(v -> {
            conversationId = null;
            adapter.clear();
            adapter.add(new ChatMessage("assistant", "New chat started. What are we working on?"));
            status.setText("● Online");
        });
        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) { sendMessage(); return true; }
            return false;
        });
    }

    private void sendMessage() {
        String text = input.getText().toString().trim();
        if (text.isEmpty()) return;
        adapter.add(new ChatMessage("user", text));
        input.setText("");
        status.setText("● Thinking…");
        findViewById(R.id.sendButton).setEnabled(false);

        Api.service.chat(new Api.ChatRequest(conversationId, deviceId, text)).enqueue(new Callback<>() {
            @Override public void onResponse(Call<Api.ChatResponse> call, Response<Api.ChatResponse> response) {
                runOnUiThread(() -> {
                    findViewById(R.id.sendButton).setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        conversationId = response.body().conversationId();
                        adapter.add(new ChatMessage("assistant", response.body().reply()));
                        status.setText("● Online");
                    } else {
                        adapter.add(new ChatMessage("assistant", "Something went wrong on the server. Please try again."));
                        status.setText("● Error");
                    }
                });
            }
            @Override public void onFailure(Call<Api.ChatResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    findViewById(R.id.sendButton).setEnabled(true);
                    adapter.add(new ChatMessage("assistant", "I can't reach Pavi AI right now. Check your connection and try again."));
                    status.setText("● Offline");
                });
            }
        });
    }
}