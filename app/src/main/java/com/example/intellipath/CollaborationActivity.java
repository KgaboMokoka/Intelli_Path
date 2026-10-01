package com.example.intellipath;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CollaborationActivity extends AppCompatActivity {

    // MAIN GROUPS

    private CardView cardWorkplaceCrew;
    private CardView cardDeveloperSquad;
    private CardView cardCareerLaunchpad;
    private CardView cardProjectPartners;

    // MAIN GROUP MEMBER COUNTS
    private TextView tvWorkplaceMembers;
    private TextView tvDeveloperMembers;
    private TextView tvCareerMembers;
    private TextView tvProjectMembers;

    private static final String KEY_MAIN_MEMBER_PREFIX =
            "main_member_count_";

    private static final String KEY_MAIN_JOINED_PREFIX =
            "main_joined_";

    // CREATE GROUP

    private CardView cardCreateGroup;
    private LinearLayout createdGroupsContainer;
    private TextView tvYourGroupsTitle;
    private TextView tvCreateGroupStatus;

    private static final int MAX_CREATED_GROUPS = 2;

    private static final String PREFS_NAME =
            "IntelliPathCollaboration";

    private static final String KEY_GROUP_COUNT =
            "group_count";

    private static final String KEY_GROUP_1_NAME =
            "group_1_name";

    private static final String KEY_GROUP_1_PURPOSE =
            "group_1_purpose";

    private static final String KEY_GROUP_2_NAME =
            "group_2_name";

    private static final String KEY_GROUP_2_PURPOSE =
            "group_2_purpose";

    // BOTTOM NAVIGATION

    private LinearLayout navDashboard;
    private LinearLayout navAssessments;
    private LinearLayout navProgress;
    private LinearLayout navProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collaboration);

        initialiseViews();
        setupClickListeners();
        loadCreatedGroups();
        loadMainGroupMemberCounts();
        updateCreateGroupStatus();
    }


    // ============================================================
    // INITIALISE VIEWS
    // ============================================================

    private void initialiseViews() {

        cardWorkplaceCrew =
                findViewById(R.id.cardWorkplaceCrew);

        cardDeveloperSquad =
                findViewById(R.id.cardDeveloperSquad);

        cardCareerLaunchpad =
                findViewById(R.id.cardCareerLaunchpad);

        cardProjectPartners =
                findViewById(R.id.cardProjectPartners);

        tvWorkplaceMembers =
                findViewById(R.id.tvWorkplaceMembers);

        tvDeveloperMembers =
                findViewById(R.id.tvDeveloperMembers);

        tvCareerMembers =
                findViewById(R.id.tvCareerMembers);

        tvProjectMembers =
                findViewById(R.id.tvProjectMembers);

        cardCreateGroup =
                findViewById(R.id.cardCreateGroup);

        createdGroupsContainer =
                findViewById(R.id.createdGroupsContainer);

        tvYourGroupsTitle =
                findViewById(R.id.tvYourGroupsTitle);

        tvCreateGroupStatus =
                findViewById(R.id.tvCreateGroupStatus);

        navDashboard =
                findViewById(R.id.navDashboard);

        navAssessments =
                findViewById(R.id.navAssessments);

        navProgress =
                findViewById(R.id.navProgress);

        navProfile =
                findViewById(R.id.navProfile);
    }


    // ============================================================
    // CLICK LISTENERS
    // ============================================================

    private void setupClickListeners() {

        cardWorkplaceCrew.setOnClickListener(
                view -> showMainGroupDialog(
                        "workplace_crew",
                        "Workplace Crew",
                        "Share interview experiences, workplace advice and practical tips for preparing for the world of work."
                )
        );

        cardDeveloperSquad.setOnClickListener(
                view -> showMainGroupDialog(
                        "developer_squad",
                        "Developer Squad",
                        "Help each other solve coding problems, discuss development projects and learn new technical skills."
                )
        );

        cardCareerLaunchpad.setOnClickListener(
                view -> showMainGroupDialog(
                        "career_launchpad",
                        "Career Launchpad",
                        "Share opportunities, career advice, portfolio tips, LinkedIn guidance and networking strategies."
                )
        );

        cardProjectPartners.setOnClickListener(
                view -> showMainGroupDialog(
                        "project_partners",
                        "Project Partners",
                        "Find students to collaborate with on university projects, assignments and software development tasks."
                )
        );

        cardCreateGroup.setOnClickListener(
                view -> showCreateGroupDialog()
        );


        navDashboard.setOnClickListener(view -> {

            Intent intent = new Intent(
                    CollaborationActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();
        });


        navAssessments.setOnClickListener(view ->
                Toast.makeText(
                        CollaborationActivity.this,
                        "Assessments selected.",
                        Toast.LENGTH_SHORT
                ).show()
        );


        navProgress.setOnClickListener(view ->
                Toast.makeText(
                        CollaborationActivity.this,
                        "Progress selected.",
                        Toast.LENGTH_SHORT
                ).show()
        );


        navProfile.setOnClickListener(view ->
                Toast.makeText(
                        CollaborationActivity.this,
                        "Profile selected.",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }


    // ============================================================
    // MAIN GROUP INFORMATION
    // ============================================================

    private void showMainGroupDialog(
            String groupId,
            String groupName,
            String groupPurpose
    ) {

        String rules =
                "Group Rules:\n\n" +
                        "1. Keep discussions related to the group's purpose.\n\n" +
                        "2. Respect all group members and their contributions.\n\n" +
                        "3. Keep discussions constructive and relevant.\n\n" +
                        "4. Do not claim another student's work as your own.\n\n" +
                        "5. Do not request or share completed assignments for submission as another student's work.\n\n" +
                        "6. No swearing, harassment, discrimination or offensive language.\n\n" +
                        "7. No unrelated advertising or spam.";

        new AlertDialog.Builder(this)
                .setTitle(groupName)
                .setMessage(
                        groupPurpose +
                                "\n\n" +
                                rules
                )
                .setPositiveButton(
                        "Open Chat",
                        (dialog, which) -> {

                            joinMainGroupAndOpenChat(
                                    groupId,
                                    groupName
                            );

                        }
                )
                .setNegativeButton(
                        "Close",
                        null
                )
                .show();
    }


    // ============================================================
    // MAIN GROUP MEMBER COUNTS
    // ============================================================

    private void loadMainGroupMemberCounts() {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        tvWorkplaceMembers.setText(
                getMemberText(
                        preferences.getInt(
                                KEY_MAIN_MEMBER_PREFIX + "workplace_crew",
                                0
                        )
                )
        );

        tvDeveloperMembers.setText(
                getMemberText(
                        preferences.getInt(
                                KEY_MAIN_MEMBER_PREFIX + "developer_squad",
                                0
                        )
                )
        );

        tvCareerMembers.setText(
                getMemberText(
                        preferences.getInt(
                                KEY_MAIN_MEMBER_PREFIX + "career_launchpad",
                                0
                        )
                )
        );

        tvProjectMembers.setText(
                getMemberText(
                        preferences.getInt(
                                KEY_MAIN_MEMBER_PREFIX + "project_partners",
                                0
                        )
                )
        );
    }


    private String getMemberText(int count) {

        return count + (count == 1 ? " member" : " members");
    }


    private void joinMainGroupAndOpenChat(
            String groupId,
            String groupName
    ) {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        boolean alreadyJoined =
                preferences.getBoolean(
                        KEY_MAIN_JOINED_PREFIX + groupId,
                        false
                );

        if (!alreadyJoined) {

            int currentCount =
                    preferences.getInt(
                            KEY_MAIN_MEMBER_PREFIX + groupId,
                            0
                    );

            preferences.edit()
                    .putInt(
                            KEY_MAIN_MEMBER_PREFIX + groupId,
                            currentCount + 1
                    )
                    .putBoolean(
                            KEY_MAIN_JOINED_PREFIX + groupId,
                            true
                    )
                    .apply();

            loadMainGroupMemberCounts();
        }

        Intent intent = new Intent(
                CollaborationActivity.this,
                TeamChatActivity.class
        );

        intent.putExtra(
                "groupId",
                groupId
        );

        intent.putExtra(
                "groupName",
                groupName
        );

        startActivity(intent);
    }


    // ============================================================
    // CREATE GROUP DIALOG
    // ============================================================

    private void showCreateGroupDialog() {

        int currentCount =
                getGroupCount();

        if (currentCount >= MAX_CREATED_GROUPS) {

            new AlertDialog.Builder(this)
                    .setTitle("Group Limit Reached")
                    .setMessage(
                            "You can create a maximum of 2 groups."
                    )
                    .setPositiveButton(
                            "OK",
                            null
                    )
                    .show();

            return;
        }


        LinearLayout form =
                new LinearLayout(this);

        form.setOrientation(
                LinearLayout.VERTICAL
        );

        form.setPadding(
                40,
                10,
                40,
                5
        );


        EditText groupNameInput =
                new EditText(this);

        groupNameInput.setHint(
                "Group name"
        );

        groupNameInput.setSingleLine(true);

        groupNameInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );


        EditText groupPurposeInput =
                new EditText(this);

        groupPurposeInput.setHint(
                "Group purpose"
        );

        groupPurposeInput.setGravity(
                Gravity.TOP
        );

        groupPurposeInput.setMinLines(3);

        groupPurposeInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );


        form.addView(
                groupNameInput
        );


        LinearLayout.LayoutParams purposeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        purposeParams.topMargin = 15;


        form.addView(
                groupPurposeInput,
                purposeParams
        );


        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Create a Group")
                        .setMessage(
                                "Create a group for IntelliPath students. " +
                                        "Your group must follow the same " +
                                        "community rules as the main groups."
                        )
                        .setView(form)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Create Group",
                                null
                        )
                        .create();


        dialog.setOnShowListener(
                dialogInterface -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(
                            view -> {

                                String groupName =
                                        groupNameInput
                                                .getText()
                                                .toString()
                                                .trim();


                                String groupPurpose =
                                        groupPurposeInput
                                                .getText()
                                                .toString()
                                                .trim();


                                if (groupName.isEmpty()) {

                                    groupNameInput.setError(
                                            "Enter a group name"
                                    );

                                    groupNameInput.requestFocus();

                                    return;
                                }


                                if (groupPurpose.isEmpty()) {

                                    groupPurposeInput.setError(
                                            "Enter the group purpose"
                                    );

                                    groupPurposeInput.requestFocus();

                                    return;
                                }


                                if (groupName.length() > 50) {

                                    groupNameInput.setError(
                                            "Maximum 50 characters"
                                    );

                                    return;
                                }


                                if (groupPurpose.length() > 250) {

                                    groupPurposeInput.setError(
                                            "Maximum 250 characters"
                                    );

                                    return;
                                }


                                saveCreatedGroup(
                                        groupName,
                                        groupPurpose
                                );


                                dialog.dismiss();
                            }
                    );
                }
        );


        dialog.show();
    }


    // ============================================================
    // SAVE GROUP
    // ============================================================

    private void saveCreatedGroup(
            String groupName,
            String groupPurpose
    ) {

        int currentCount =
                getGroupCount();


        if (currentCount >= MAX_CREATED_GROUPS) {

            Toast.makeText(
                    this,
                    "You can only create 2 groups.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        int newGroupNumber =
                currentCount + 1;


        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );


        SharedPreferences.Editor editor =
                preferences.edit();


        if (newGroupNumber == 1) {

            editor.putString(
                    KEY_GROUP_1_NAME,
                    groupName
            );

            editor.putString(
                    KEY_GROUP_1_PURPOSE,
                    groupPurpose
            );

        } else {

            editor.putString(
                    KEY_GROUP_2_NAME,
                    groupName
            );

            editor.putString(
                    KEY_GROUP_2_PURPOSE,
                    groupPurpose
            );
        }


        editor.putInt(
                KEY_GROUP_COUNT,
                newGroupNumber
        );


        editor.apply();


        addCreatedGroupCard(
                groupName,
                groupPurpose,
                newGroupNumber
        );


        tvYourGroupsTitle.setVisibility(
                View.VISIBLE
        );


        updateCreateGroupStatus();


        Toast.makeText(
                this,
                "Group created successfully.",
                Toast.LENGTH_SHORT
        ).show();
    }


    // ============================================================
    // LOAD CREATED GROUPS
    // ============================================================

    private void loadCreatedGroups() {

        createdGroupsContainer.removeAllViews();


        int groupCount =
                getGroupCount();


        if (groupCount == 0) {

            tvYourGroupsTitle.setVisibility(
                    View.GONE
            );

            return;
        }


        tvYourGroupsTitle.setVisibility(
                View.VISIBLE
        );


        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );


        if (groupCount >= 1) {

            String name =
                    preferences.getString(
                            KEY_GROUP_1_NAME,
                            ""
                    );

            String purpose =
                    preferences.getString(
                            KEY_GROUP_1_PURPOSE,
                            ""
                    );


            if (!name.isEmpty()) {

                addCreatedGroupCard(
                        name,
                        purpose,
                        1
                );
            }
        }


        if (groupCount >= 2) {

            String name =
                    preferences.getString(
                            KEY_GROUP_2_NAME,
                            ""
                    );

            String purpose =
                    preferences.getString(
                            KEY_GROUP_2_PURPOSE,
                            ""
                    );


            if (!name.isEmpty()) {

                addCreatedGroupCard(
                        name,
                        purpose,
                        2
                );
            }
        }
    }


    // ============================================================
    // GET GROUP COUNT
    // ============================================================

    private int getGroupCount() {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );


        return preferences.getInt(
                KEY_GROUP_COUNT,
                0
        );
    }


    // ============================================================
    // ADD CREATED GROUP CARD
    // ============================================================

    private void addCreatedGroupCard(
            String groupName,
            String groupPurpose,
            int groupNumber
    ) {

        CardView card =
                new CardView(this);

        card.setRadius(14f);

        card.setCardElevation(2f);

        card.setCardBackgroundColor(
                Color.WHITE
        );


        LinearLayout content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                25,
                20,
                25,
                20
        );


        TextView icon =
                new TextView(this);

        icon.setText("👥");

        icon.setTextSize(20);

        icon.setGravity(
                Gravity.CENTER
        );

        icon.setBackgroundColor(
                Color.rgb(
                        238,
                        234,
                        251
                )
        );


        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(
                        50,
                        50
                );


        content.addView(
                icon,
                iconParams
        );


        TextView name =
                new TextView(this);

        name.setText(
                groupName
        );

        name.setTextColor(
                Color.rgb(
                        23,
                        26,
                        52
                )
        );

        name.setTextSize(15);

        name.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        nameParams.topMargin = 10;


        content.addView(
                name,
                nameParams
        );


        TextView purpose =
                new TextView(this);

        purpose.setText(
                groupPurpose
        );

        purpose.setTextColor(
                Color.rgb(
                        115,
                        117,
                        134
                )
        );

        purpose.setTextSize(10);


        LinearLayout.LayoutParams purposeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        purposeParams.topMargin = 5;


        content.addView(
                purpose,
                purposeParams
        );


        TextView groupNumberText =
                new TextView(this);

        groupNumberText.setText(
                "Created by you • Group " +
                        groupNumber +
                        " of 2"
        );

        groupNumberText.setTextColor(
                Color.rgb(
                        103,
                        68,
                        183
                )
        );

        groupNumberText.setTextSize(9);


        LinearLayout.LayoutParams numberParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        numberParams.topMargin = 10;


        content.addView(
                groupNumberText,
                numberParams
        );


        TextView rules =
                new TextView(this);

        rules.setText(
                "View Group Rules →"
        );

        rules.setTextColor(
                Color.rgb(
                        103,
                        68,
                        183
                )
        );

        rules.setTextSize(10);

        rules.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );


        LinearLayout.LayoutParams rulesParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rulesParams.topMargin = 8;


        content.addView(
                rules,
                rulesParams
        );


        card.addView(
                content
        );


        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.topMargin = 10;


        createdGroupsContainer.addView(
                card,
                cardParams
        );


        card.setOnClickListener(
                view -> showCreatedGroupRules(
                        groupName,
                        groupPurpose
                )
        );
    }


    // ============================================================
    // CREATED GROUP RULES
    // ============================================================

    private void showCreatedGroupRules(
            String groupName,
            String groupPurpose
    ) {

        String rules =
                "1. Keep discussions related to the group's purpose.\n\n" +
                        "2. Respect all group members and their contributions.\n\n" +
                        "3. Keep discussions constructive and relevant.\n\n" +
                        "4. Do not claim another student's work as your own.\n\n" +
                        "5. Do not request or share completed assignments for submission as another student's work.\n\n" +
                        "6. No swearing, harassment, discrimination or offensive language.\n\n" +
                        "7. No unrelated advertising or spam.";


        new AlertDialog.Builder(this)
                .setTitle(groupName)
                .setMessage(
                        "Purpose:\n" +
                                groupPurpose +
                                "\n\n" +
                                rules
                )
                .setPositiveButton(
                        "Open Chat",
                        (dialog, which) -> {

                            Intent intent = new Intent(
                                    CollaborationActivity.this,
                                    TeamChatActivity.class
                            );

                            intent.putExtra(
                                    "groupId",
                                    "created_group_" + groupName
                            );

                            intent.putExtra(
                                    "groupName",
                                    groupName
                            );

                            startActivity(intent);
                        }
                )
                .setNegativeButton(
                        "Close",
                        null
                )
                .show();
    }


    // ============================================================
    // UPDATE STATUS
    // ============================================================

    private void updateCreateGroupStatus() {

        int currentCount =
                getGroupCount();


        int remaining =
                MAX_CREATED_GROUPS -
                        currentCount;


        if (remaining <= 0) {

            tvCreateGroupStatus.setText(
                    "You have reached your 2-group limit."
            );

        } else {

            tvCreateGroupStatus.setText(
                    "You can create " +
                            remaining +
                            " more group" +
                            (remaining == 1
                                    ? "."
                                    : "s.")
            );
        }
    }
}
