package com.example.intellipath;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TeamChatActivity extends AppCompatActivity {

    private LinearLayout messagesContainer;
    private ScrollView chatScrollView;
    private EditText etMessage;
    private TextView btnSend;
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_team_chat);

        initialiseViews();
        setupButtons();
        setupSendMessage();
    }

    private void initialiseViews() {

        messagesContainer = findViewById(R.id.messagesContainer);
        chatScrollView = findViewById(R.id.chatScrollView);

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
    }


    private void setupButtons() {

        // Back to collaboration groups

        btnBack.setOnClickListener(v -> {
            finish();
        });


        // Group information

        TextView btnGroupInfo = findViewById(R.id.btnGroupInfo);

        btnGroupInfo.setOnClickListener(v -> {
            finish();
        });


        // Dashboard

        findViewById(R.id.navDashboard).setOnClickListener(v -> {

            android.content.Intent intent =
                    new android.content.Intent(
                            TeamChatActivity.this,
                            MainActivity.class
                    );

            intent.addFlags(
                    android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
            );

            startActivity(intent);
            finish();
        });


        // Profile

        findViewById(R.id.navProfile).setOnClickListener(v -> {

            // Profile navigation can be connected later.
        });


        // Assessments

        findViewById(R.id.navAssessments).setOnClickListener(v -> {

            // Connect to your existing assessment page later.
        });


        // Progress

        findViewById(R.id.navProgress).setOnClickListener(v -> {

            // Connect to your existing progress page later.
        });
    }


    private void setupSendMessage() {

        btnSend.setOnClickListener(v -> {

            String message = etMessage.getText()
                    .toString()
                    .trim();

            if (message.isEmpty()) {
                return;
            }

            addMessage(
                    "You",
                    message,
                    true
            );

            etMessage.setText("");

            scrollToBottom();

            // Hide keyboard

            InputMethodManager keyboard =
                    (InputMethodManager)
                            getSystemService(
                                    Context.INPUT_METHOD_SERVICE
                            );

            if (keyboard != null) {

                keyboard.hideSoftInputFromWindow(
                        etMessage.getWindowToken(),
                        0
                );
            }
        });
    }


    /**
     * Adds a new message to the chat.
     *
     * outgoing = true means the message belongs to
     * the current logged-in user.
     */
    private void addMessage(
            String sender,
            String message,
            boolean outgoing
    ) {

        LinearLayout messageRow =
                new LinearLayout(this);

        messageRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        messageRow.setGravity(
                outgoing
                        ? Gravity.END
                        : Gravity.START
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rowParams.topMargin = dp(10);

        messageRow.setLayoutParams(rowParams);


        TextView messageBubble =
                new TextView(this);

        messageBubble.setText(message);

        messageBubble.setTextSize(8);

        messageBubble.setTextColor(
                Color.parseColor(
                        outgoing
                                ? "#4D3274"
                                : "#343545"
                )
        );

        messageBubble.setPadding(
                dp(10),
                dp(8),
                dp(10),
                dp(8)
        );


        GradientDrawable bubble =
                new GradientDrawable();

        bubble.setCornerRadius(
                dp(14)
        );

        bubble.setColor(
                Color.parseColor(
                        outgoing
                                ? "#E8D8FF"
                                : "#FFFFFF"
                )
        );

        messageBubble.setBackground(bubble);


        LinearLayout.LayoutParams bubbleParams =
                new LinearLayout.LayoutParams(
                        dp(235),
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        messageBubble.setLayoutParams(
                bubbleParams
        );

        messageRow.addView(
                messageBubble
        );

        messagesContainer.addView(
                messageRow
        );
    }


    private void scrollToBottom() {

        chatScrollView.post(() ->
                chatScrollView.fullScroll(
                        View.FOCUS_DOWN
                )
        );
    }


    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int)
                (value * density + 0.5f);
    }
}