package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GroupInformationActivity extends AppCompatActivity {

    // ============================================================
    // VIEWS
    // ============================================================

    private TextView tvGroupName;
    private TextView tvGroupDescription;
    private TextView tvGroupPurpose;
    private TextView tvGroupRules;
    private TextView tvMemberCount;
    private TextView tvGroupIcon;

    private Button btnJoinGroup;
    private Button btnBackToGroups;

    // ============================================================
    // GROUP INFORMATION
    // ============================================================

    private String groupId;
    private String groupName;

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_information);

        initialiseViews();

        // Get selected group from CollaborationActivity
        groupId = getIntent().getStringExtra("groupId");

        if (groupId == null || groupId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Unable to load group information.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        loadGroupInformation();

        setupClickListeners();
    }

    // ============================================================
    // INITIALISE VIEWS
    // ============================================================

    private void initialiseViews() {

        tvGroupName = findViewById(R.id.tvGroupName);
        tvGroupDescription = findViewById(R.id.tvGroupDescription);
        tvGroupPurpose = findViewById(R.id.tvGroupPurpose);
        tvGroupRules = findViewById(R.id.tvGroupRules);
        tvMemberCount = findViewById(R.id.tvMemberCount);
        tvGroupIcon = findViewById(R.id.tvGroupIcon);

        btnJoinGroup = findViewById(R.id.btnJoinGroup);
        btnBackToGroups = findViewById(R.id.btnBackToGroups);
    }

    // ============================================================
    // LOAD GROUP INFORMATION
    // ============================================================

    private void loadGroupInformation() {

        switch (groupId) {

            // ====================================================
            // WORKPLACE CREW
            // ====================================================

            case "workplace_crew":

                groupName = "Workplace Crew";

                tvGroupIcon.setText("💼");

                tvGroupName.setText("Workplace Crew");

                tvGroupDescription.setText(
                        "A space for students to share interview experiences, "
                                + "workplace advice and practical tips for preparing "
                                + "for the world of work."
                );

                tvGroupPurpose.setText(
                        "• Share interview experiences and preparation tips.\n\n"
                                + "• Discuss workplace expectations and professional behaviour.\n\n"
                                + "• Ask questions about adapting to a professional environment.\n\n"
                                + "• Share lessons learned from internships, graduate programmes "
                                + "and workplace experiences.\n\n"
                                + "• Support other students as they prepare for employment."
                );

                tvMemberCount.setText("24 members");

                tvGroupRules.setText(
                        "1. Keep discussions focused on workplace and professional topics.\n\n"
                                + "2. Share interview experiences honestly and respectfully.\n\n"
                                + "3. Do not share confidential information about employers "
                                + "or interview processes.\n\n"
                                + "4. Do not insult, mock or discourage other members.\n\n"
                                + "5. Give constructive advice when responding to others.\n\n"
                                + "6. No swearing, harassment, discrimination or offensive language.\n\n"
                                + "7. Do not use the group for unrelated advertising or spam."
                );

                break;


            // ====================================================
            // DEVELOPER SQUAD
            // ====================================================

            case "developer_squad":

                groupName = "Developer Squad";

                tvGroupIcon.setText("</>");

                tvGroupName.setText("Developer Squad");

                tvGroupDescription.setText(
                        "A technical community where students can help each other "
                                + "with coding problems, software projects, debugging "
                                + "and development skills."
                );

                tvGroupPurpose.setText(
                        "• Ask questions about programming and software development.\n\n"
                                + "• Share solutions to coding problems.\n\n"
                                + "• Discuss development tools, frameworks and technologies.\n\n"
                                + "• Help each other debug projects.\n\n"
                                + "• Share useful learning resources and technical advice.\n\n"
                                + "• Collaborate on software development challenges."
                );

                tvMemberCount.setText("31 members");

                tvGroupRules.setText(
                        "1. Keep discussions related to programming and software development.\n\n"
                                + "2. Explain solutions instead of simply giving someone their "
                                + "entire assignment.\n\n"
                                + "3. Respect different levels of programming experience.\n\n"
                                + "4. Do not deliberately share malicious or harmful code.\n\n"
                                + "5. Do not share passwords, API keys or other private credentials.\n\n"
                                + "6. No swearing, harassment, discrimination or offensive language.\n\n"
                                + "7. Give constructive feedback when reviewing someone's code.\n\n"
                                + "8. Do not spam unrelated content."
                );

                break;


            // ====================================================
            // CAREER LAUNCHPAD
            // ====================================================

            case "career_launchpad":

                groupName = "Career Launchpad";

                tvGroupIcon.setText("🚀");

                tvGroupName.setText("Career Launchpad");

                tvGroupDescription.setText(
                        "A career-focused community for students preparing to "
                                + "transition from university into the workplace."
                );

                tvGroupPurpose.setText(
                        "• Share graduate programme and internship opportunities.\n\n"
                                + "• Discuss CVs, portfolios and LinkedIn profiles.\n\n"
                                + "• Share career advice and job-search strategies.\n\n"
                                + "• Discuss networking opportunities and professional events.\n\n"
                                + "• Help students prepare for applications and interviews.\n\n"
                                + "• Support one another through the transition from varsity to work."
                );

                tvMemberCount.setText("28 members");

                tvGroupRules.setText(
                        "1. Keep discussions focused on careers, opportunities and "
                                + "professional development.\n\n"
                                + "2. Verify opportunities before sharing them with the group.\n\n"
                                + "3. Do not share misleading or fraudulent job opportunities.\n\n"
                                + "4. Respect different career goals and professional interests.\n\n"
                                + "5. Do not share another person's CV, contact details or "
                                + "personal information without permission.\n\n"
                                + "6. No swearing, harassment, discrimination or offensive language.\n\n"
                                + "7. Give constructive feedback on CVs, portfolios and career questions.\n\n"
                                + "8. Do not spam the group with unrelated advertisements."
                );

                break;


            // ====================================================
            // PROJECT PARTNERS
            // ====================================================

            case "project_partners":

                groupName = "Project Partners";

                tvGroupIcon.setText("🤝");

                tvGroupName.setText("Project Partners");

                tvGroupDescription.setText(
                        "A collaboration space where students can find partners "
                                + "for university projects, assignments and software "
                                + "development tasks."
                );

                tvGroupPurpose.setText(
                        "• Find students with complementary skills.\n\n"
                                + "• Form teams for university projects and assignments.\n\n"
                                + "• Share project ideas and discuss possible approaches.\n\n"
                                + "• Find assistance with specific technical areas of a project.\n\n"
                                + "• Discuss project planning, task allocation and teamwork.\n\n"
                                + "• Support each other in completing academic projects."
                );

                tvMemberCount.setText("19 members");

                tvGroupRules.setText(
                        "1. Keep discussions related to university projects and assignments.\n\n"
                                + "2. Clearly communicate your project requirements when looking "
                                + "for collaborators.\n\n"
                                + "3. Respect project deadlines and other students' availability.\n\n"
                                + "4. Do not claim another student's work as your own.\n\n"
                                + "5. Do not request or share completed assignments for submission "
                                + "as another student's work.\n\n"
                                + "6. Respect everyone's contribution to a project.\n\n"
                                + "7. No swearing, harassment, discrimination or offensive language.\n\n"
                                + "8. Do not use the group for unrelated advertising or spam."
                );

                break;


            // ====================================================
            // UNKNOWN GROUP
            // ====================================================

            default:

                Toast.makeText(
                        this,
                        "Unknown collaboration group.",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
                break;
        }
    }

    // ============================================================
    // CLICK LISTENERS
    // ============================================================

    private void setupClickListeners() {

        // --------------------------------------------------------
        // JOIN GROUP
        // --------------------------------------------------------

        btnJoinGroup.setOnClickListener(view -> {

            /*
             * Database membership functionality will be connected
             * here later.
             *
             * For now, we move to the future Group Chat screen.
             */

            Toast.makeText(
                    GroupInformationActivity.this,
                    "You joined " + groupName + ".",
                    Toast.LENGTH_SHORT
            ).show();

            /*
             * GroupChatActivity will be connected in the next stage.
             */
        });


        // --------------------------------------------------------
        // BACK TO GROUPS
        // --------------------------------------------------------

        btnBackToGroups.setOnClickListener(view -> {

            finish();
        });
    }
}
