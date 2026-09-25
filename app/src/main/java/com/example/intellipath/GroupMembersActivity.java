package com.example.intellipath;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GroupMembersActivity extends AppCompatActivity {

    private Button btnLeaveGroup;
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_members);

        initialiseViews();
        setupButtons();
    }


    private void initialiseViews() {

        btnLeaveGroup = findViewById(R.id.btnLeaveGroup);
        btnBack = findViewById(R.id.btnBack);
    }


    private void setupButtons() {

        // -----------------------------------------
        // BACK
        // -----------------------------------------

        btnBack.setOnClickListener(v -> {
            finish();
        });


        // -----------------------------------------
        // LEAVE GROUP
        // -----------------------------------------

        btnLeaveGroup.setOnClickListener(v -> {

            showLeaveGroupDialog();

        });


        // -----------------------------------------
        // COMMUNITY GUIDELINES
        // -----------------------------------------

        findViewById(R.id.btnCommunityGuidelines)
                .setOnClickListener(v -> {

                    showCommunityGuidelines();

                });
    }


    /**
     * Displays the Leave Group confirmation popup.
     */
    private void showLeaveGroupDialog() {

        final AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .create();

        LinearLayout mainLayout =
                new LinearLayout(this);

        mainLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        mainLayout.setPadding(
                dp(22),
                dp(20),
                dp(22),
                dp(18)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                dp(12)
        );

        mainLayout.setBackground(
                background
        );


        // -----------------------------------------
        // ICON
        // -----------------------------------------

        TextView icon =
                new TextView(this);

        icon.setText("⇥");

        icon.setTextSize(23);

        icon.setTextColor(
                Color.parseColor("#D22A2A")
        );

        icon.setGravity(
                Gravity.CENTER
        );

        GradientDrawable iconBackground =
                new GradientDrawable();

        iconBackground.setColor(
                Color.parseColor("#FFDADA")
        );

        iconBackground.setShape(
                GradientDrawable.OVAL
        );

        icon.setBackground(
                iconBackground
        );

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        dp(45),
                        dp(45)
                );

        mainLayout.addView(
                icon,
                iconParams
        );


        // -----------------------------------------
        // TITLE
        // -----------------------------------------

        TextView title =
                new TextView(this);

        title.setText(
                "Leave this group?"
        );

        title.setTextSize(18);

        title.setTextColor(
                Color.parseColor("#17182A")
        );

        title.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.topMargin =
                dp(14);

        mainLayout.addView(
                title,
                titleParams
        );


        // -----------------------------------------
        // MESSAGE
        // -----------------------------------------

        TextView message =
                new TextView(this);

        message.setText(
                "You will no longer receive messages or updates from Career Launchpad. You can rejoin this group at any time from the Collaboration home page."
        );

        message.setTextSize(10);

        message.setTextColor(
                Color.parseColor("#5E5F6B")
        );

        message.setLineSpacing(
                dp(2),
                1.0f
        );

        LinearLayout.LayoutParams messageParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        messageParams.topMargin =
                dp(8);

        mainLayout.addView(
                message,
                messageParams
        );


        // -----------------------------------------
        // BUTTON ROW
        // -----------------------------------------

        LinearLayout buttonRow =
                new LinearLayout(this);

        buttonRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        buttonRow.setGravity(
                Gravity.END | Gravity.CENTER_VERTICAL
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(45)
                );

        rowParams.topMargin =
                dp(12);

        mainLayout.addView(
                buttonRow,
                rowParams
        );


        // CANCEL

        TextView cancelButton =
                new TextView(this);

        cancelButton.setText(
                "Cancel"
        );

        cancelButton.setTextSize(10);

        cancelButton.setTextColor(
                Color.parseColor("#333344")
        );

        cancelButton.setGravity(
                Gravity.CENTER
        );

        cancelButton.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        cancelButton.setClickable(true);

        LinearLayout.LayoutParams cancelParams =
                new LinearLayout.LayoutParams(
                        dp(70),
                        dp(40)
                );

        buttonRow.addView(
                cancelButton,
                cancelParams
        );


        // LEAVE

        Button leaveButton =
                new Button(this);

        leaveButton.setText(
                "Leave Group"
        );

        leaveButton.setTextSize(9);

        leaveButton.setTextColor(
                Color.WHITE
        );

        leaveButton.setAllCaps(false);

        leaveButton.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        leaveButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#C62828")
                )
        );

        LinearLayout.LayoutParams leaveParams =
                new LinearLayout.LayoutParams(
                        dp(110),
                        dp(42)
                );

        leaveParams.leftMargin =
                dp(5);

        buttonRow.addView(
                leaveButton,
                leaveParams
        );


        // -----------------------------------------
        // BUTTON ACTIONS
        // -----------------------------------------

        cancelButton.setOnClickListener(v -> {

            dialog.dismiss();

        });


        leaveButton.setOnClickListener(v -> {

            dialog.dismiss();

            leaveGroup();

        });


        // -----------------------------------------
        // SHOW
        // -----------------------------------------

        dialog.setView(
                mainLayout
        );

        dialog.show();

        if (dialog.getWindow() != null) {

            dialog.getWindow()
                    .setBackgroundDrawableResource(
                            android.R.color.transparent
                    );

            dialog.getWindow()
                    .setLayout(
                            dp(310),
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
        }
    }


    /**
     * Temporary leave-group behaviour.
     *
     * For now this returns the user to the
     * Collaboration page.
     *
     * Later we can will connect this to Supabase.
     */
    private void leaveGroup() {

        Intent intent =
                new Intent(
                        GroupMembersActivity.this,
                        CollaborationActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        startActivity(intent);

        finish();
    }


    /**
     * Community Guidelines popup.
     */
    private void showCommunityGuidelines() {

        new AlertDialog.Builder(this)
                .setTitle("Community Guidelines")
                .setMessage(
                        "1. Be respectful and professional.\n\n" +
                                "2. No swearing, insults or harassment.\n\n" +
                                "3. Do not bully, threaten or discriminate.\n\n" +
                                "4. Keep discussions relevant to the group.\n\n" +
                                "5. Do not spam or post repetitive content.\n\n" +
                                "6. Protect other members' privacy.\n\n" +
                                "7. Give constructive feedback.\n\n" +
                                "8. Share appropriate and useful information.\n\n" +
                                "9. No unrelated commercial advertising.\n\n" +
                                "10. Use the community to learn, collaborate and support others."
                )
                .setPositiveButton(
                        "Got it",
                        null
                )
                .show();
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