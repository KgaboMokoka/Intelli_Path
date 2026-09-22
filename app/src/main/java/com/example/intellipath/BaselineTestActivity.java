package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaselineTestActivity extends AppCompatActivity {

    private TextView tvQuestionNumber;
    private TextView tvTimer;
    private TextView tvQuestion;

    private ProgressBar questionProgress;
    private RadioGroup answerGroup;

    private Button btnPrevious;
    private Button btnNext;

    private List<Question> questionList;
    private int currentQuestion = 0;

    // Store selected answer for each question.
    // -1 means no answer has been selected.
    private int[] selectedAnswers;

    // 1 hour
    private static final long TEST_DURATION = 60 * 60 * 1000;

    private CountDownTimer countDownTimer;

    // Record when the assessment starts
    private long testStartTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_baseline_test);


        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestion = findViewById(R.id.tvQuestion);

        questionProgress = findViewById(R.id.questionProgress);
        answerGroup = findViewById(R.id.answerGroup);

        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        // create the baseline assessment
        createQuestions();

        // answer storage
        selectedAnswers = new int[questionList.size()];

        for (int i = 0; i < selectedAnswers.length; i++) {
            selectedAnswers[i] = -1;
        }

        // Progress bar
        questionProgress.setMax(questionList.size());

        // Record the time the assessment starts
        testStartTime = System.currentTimeMillis();

        // Load the first question
        loadQuestion();

        // Start timer
        startTimer();


        // Previous button
        btnPrevious.setOnClickListener(v -> {

            saveCurrentAnswer();

            if (currentQuestion > 0) {
                currentQuestion--;
                loadQuestion();
            }
        });


        // Next button
        btnNext.setOnClickListener(v -> {

            // Make sure an answer has been picked
            if (answerGroup.getCheckedRadioButtonId() == -1) {

                Toast.makeText(
                        BaselineTestActivity.this,
                        "Please select an answer before continuing.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            saveCurrentAnswer();

            if (currentQuestion < questionList.size() - 1) {

                currentQuestion++;
                loadQuestion();

            } else {

                // Last question completed
                calculateResults();
            }
        });
    }


    private void createQuestions() {

        questionList = new ArrayList<>();



        // SECTION 1  GIT & VERSION CONTROL


        questionList.add(
                new Question(
                        "What is the primary purpose of Git?",
                        "Git & Version Control",
                        new String[]{
                                "To design user interfaces.",
                                "To track changes to files and coordinate work between developers.",
                                "To host websites automatically.",
                                "To compile Java programs."
                        },
                        1
                )
        );

        questionList.add(
                new Question(
                        "Which Git command is used to create a new branch?",
                        "Git & Version Control",
                        new String[]{
                                "git branch",
                                "git commit",
                                "git merge",
                                "git push"
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What does git commit do?",
                        "Git & Version Control",
                        new String[]{
                                "Deletes the repository.",
                                "Downloads a remote repository.",
                                "Records staged changes in the repository history.",
                                "Creates a new GitHub account."
                        },
                        2
                )
        );

        questionList.add(
                new Question(
                        "What is the purpose of git pull?",
                        "Git & Version Control",
                        new String[]{
                                "To retrieve changes from a remote repository and integrate them into the current branch.",
                                "To permanently delete remote branches.",
                                "To create a new Java class.",
                                "To rename the Git repository."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What is a merge conflict?",
                        "Git & Version Control",
                        new String[]{
                                "A problem caused when Git cannot automatically combine conflicting changes.",
                                "A failure to connect to the internet.",
                                "A Java syntax error.",
                                "A database connection failure."
                        },
                        0
                )
        );



        // SECTION 2  DATA STRUCTURES & ALGORITHMS


        questionList.add(
                new Question(
                        "Which data structure follows the LIFO principle?",
                        "Data Structures & Algorithms",
                        new String[]{
                                "Queue",
                                "Stack",
                                "Array",
                                "Graph"
                        },
                        1
                )
        );

        questionList.add(
                new Question(
                        "Which data structure follows the FIFO principle?",
                        "Data Structures & Algorithms",
                        new String[]{
                                "Stack",
                                "Queue",
                                "Tree",
                                "Hash table"
                        },
                        1
                )
        );

        questionList.add(
                new Question(
                        "What is the average time complexity of searching for a key in a hash table?",
                        "Data Structures & Algorithms",
                        new String[]{
                                "O(1)",
                                "O(n)",
                                "O(n²)",
                                "O(log n)"
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "Which algorithm is commonly used to find the shortest path in a weighted graph with non-negative edge weights?",
                        "Data Structures & Algorithms",
                        new String[]{
                                "Bubble Sort",
                                "Binary Search",
                                "Dijkstra's Algorithm",
                                "Linear Search"
                        },
                        2
                )
        );

        questionList.add(
                new Question(
                        "What does Big-O notation describe?",
                        "Data Structures & Algorithms",
                        new String[]{
                                "The programming language used by a system.",
                                "The growth rate of an algorithm's time or space requirements.",
                                "The number of developers on a project.",
                                "The amount of memory installed in a computer."
                        },
                        1
                )
        );



        // SECTION 3 DATABASES & SQL


        questionList.add(
                new Question(
                        "What is the purpose of a primary key in a relational database?",
                        "Databases & SQL",
                        new String[]{
                                "To uniquely identify each row in a table.",
                                "To encrypt the database.",
                                "To connect the database to the internet.",
                                "To delete duplicate tables."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "Which SQL statement is used to retrieve data?",
                        "Databases & SQL",
                        new String[]{
                                "INSERT",
                                "UPDATE",
                                "SELECT",
                                "DELETE"
                        },
                        2
                )
        );

        questionList.add(
                new Question(
                        "What is the purpose of a foreign key?",
                        "Databases & SQL",
                        new String[]{
                                "To store encrypted passwords.",
                                "To establish a relationship between tables.",
                                "To replace every primary key.",
                                "To improve monitor resolution."
                        },
                        1
                )
        );

        questionList.add(
                new Question(
                        "Which SQL clause is used to filter rows?",
                        "Databases & SQL",
                        new String[]{
                                "ORDER BY",
                                "GROUP BY",
                                "WHERE",
                                "JOIN"
                        },
                        2
                )
        );

        questionList.add(
                new Question(
                        "What is database normalization primarily intended to reduce?",
                        "Databases & SQL",
                        new String[]{
                                "Network bandwidth.",
                                "Data redundancy and update anomalies.",
                                "CPU clock speed.",
                                "The number of users."
                        },
                        1
                )
        );



        // SECTION 4 SOFTWARE ENGINEERING


        questionList.add(
                new Question(
                        "What is the main purpose of software requirements?",
                        "Software Engineering",
                        new String[]{
                                "To define what the system should do and the constraints it must satisfy.",
                                "To determine the colour of a developer's computer.",
                                "To automatically write all source code.",
                                "To replace software testing."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What is the purpose of a software development life cycle (SDLC)?",
                        "Software Engineering",
                        new String[]{
                                "To provide a structured process for planning, developing, testing, deploying and maintaining software.",
                                "To increase internet speed.",
                                "To eliminate the need for developers.",
                                "To manage only computer hardware."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What is modularity in software engineering?",
                        "Software Engineering",
                        new String[]{
                                "Combining all code into one large method.",
                                "Dividing software into smaller, manageable and relatively independent components.",
                                "Removing all documentation.",
                                "Using only one programming language."
                        },
                        1
                )
        );

        questionList.add(
                new Question(
                        "What is technical debt?",
                        "Software Engineering",
                        new String[]{
                                "Money owed for purchasing software.",
                                "The future cost or effort caused by choosing quick or inferior technical solutions.",
                                "A database storage limit.",
                                "A type of computer virus."
                        },
                        1
                )
        );

        questionList.add(
                new Question(
                        "What is the purpose of software architecture?",
                        "Software Engineering",
                        new String[]{
                                "To define the high-level structure and relationships between major system components.",
                                "To write every line of source code.",
                                "To replace user requirements.",
                                "To manage employee salaries."
                        },
                        0
                )
        );



        // SECTION 5 TESTING &DEBUGGING


        questionList.add(
                new Question(
                        "What is unit testing?",
                        "Testing & Debugging",
                        new String[]{
                                "Testing an entire company network.",
                                "Testing individual units or components of software in isolation.",
                                "Testing only the user interface.",
                                "Testing hardware temperatures."
                        },
                        1
                )
        );

        questionList.add(
                new Question(
                        "What is a regression test?",
                        "Testing & Debugging",
                        new String[]{
                                "A test performed to ensure existing functionality still works after changes.",
                                "A test that only checks spelling.",
                                "A test that deletes previous versions.",
                                "A test that measures internet speed."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What is debugging?",
                        "Testing & Debugging",
                        new String[]{
                                "The process of identifying and fixing defects in software.",
                                "The process of designing a logo.",
                                "The process of installing a monitor.",
                                "The process of creating a database backup."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What is the purpose of integration testing?",
                        "Testing & Debugging",
                        new String[]{
                                "To test whether individual components work correctly together.",
                                "To test only one variable.",
                                "To replace all unit tests.",
                                "To measure employee performance."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What is a test case?",
                        "Testing & Debugging",
                        new String[]{
                                "A documented set of inputs, conditions and expected results used to test a system.",
                                "A physical computer case.",
                                "A database table.",
                                "A Git branch."
                        },
                        0
                )
        );



        // SECTION 6 WEB & HTTP


        questionList.add(
                new Question(
                        "What does HTTP stand for?",
                        "Web & HTTP",
                        new String[]{
                                "HyperText Transfer Protocol",
                                "High Technology Transfer Program",
                                "Hyperlink Text Processing",
                                "Host Transfer Technology Protocol"
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "Which HTTP status code normally indicates a successful request?",
                        "Web & HTTP",
                        new String[]{
                                "404",
                                "500",
                                "200",
                                "301"
                        },
                        2
                )
        );

        questionList.add(
                new Question(
                        "What is REST commonly used for?",
                        "Web & HTTP",
                        new String[]{
                                "Designing computer hardware.",
                                "Building web APIs that use HTTP-based resources and operations.",
                                "Compiling Java bytecode.",
                                "Formatting hard drives."
                        },
                        1
                )
        );

        questionList.add(
                new Question(
                        "What does a 404 HTTP status code generally mean?",
                        "Web & HTTP",
                        new String[]{
                                "The request was successful.",
                                "The server has permanently shut down.",
                                "The requested resource could not be found.",
                                "The user has been authenticated."
                        },
                        2
                )
        );

        questionList.add(
                new Question(
                        "What is the primary purpose of HTTPS?",
                        "Web & HTTP",
                        new String[]{
                                "To make websites load without a server.",
                                "To provide encrypted communication between a client and server.",
                                "To replace HTML.",
                                "To increase the physical speed of a network."
                        },
                        1
                )
        );



        // SECTION7 NETWORKING & CLOUD


        questionList.add(
                new Question(
                        "What is the primary purpose of DNS?",
                        "Networking & Cloud",
                        new String[]{
                                "To translate domain names into IP addresses.",
                                "To encrypt all files on a computer.",
                                "To compile source code.",
                                "To store application passwords."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What is SSH commonly used for?",
                        "Networking & Cloud",
                        new String[]{
                                "Creating graphical user interfaces.",
                                "Secure remote communication and administration of computers.",
                                "Designing databases.",
                                "Compressing images."
                        },
                        1
                )
        );

        questionList.add(
                new Question(
                        "What does an IP address identify?",
                        "Networking & Cloud",
                        new String[]{
                                "A device or network interface on a network.",
                                "A programming language.",
                                "A database table.",
                                "A source-code function."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What is cloud computing?",
                        "Networking & Cloud",
                        new String[]{
                                "Using only local computer hardware.",
                                "Using remote computing resources and services over a network.",
                                "Writing code without an operating system.",
                                "Replacing all software with hardware."
                        },
                        1
                )
        );

        questionList.add(
                new Question(
                        "What is cloud storage?",
                        "Networking & Cloud",
                        new String[]{
                                "A physical hard drive installed inside every computer.",
                                "A remote service used to store and access data over a network.",
                                "A programming framework.",
                                "A type of CPU."
                        },
                        1
                )
        );



        // SECTION 8 PROFESSIONAL & DEVELOPMENT PRACTICES


        questionList.add(
                new Question(
                        "What is the main purpose of a code review?",
                        "Professional & Development Practices",
                        new String[]{
                                "To identify defects and improve code quality before or after changes are integrated.",
                                "To prevent developers from using version control.",
                                "To automatically deploy every application.",
                                "To replace software requirements."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What is a key characteristic of Agile software development?",
                        "Professional & Development Practices",
                        new String[]{
                                "It focuses on incremental delivery and responding to changing requirements.",
                                "It requires all requirements to remain unchanged.",
                                "It eliminates communication with stakeholders.",
                                "It only allows one developer to work on a project."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "Why is effective communication important in a software development team?",
                        "Professional & Development Practices",
                        new String[]{
                                "It helps team members understand requirements, coordinate work and resolve problems.",
                                "It removes the need for testing.",
                                "It guarantees that software will never contain bugs.",
                                "It eliminates the need for documentation."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What is requirements gathering?",
                        "Professional & Development Practices",
                        new String[]{
                                "The process of identifying and understanding what users and stakeholders need from a system.",
                                "The process of deleting old source code.",
                                "The process of installing a programming language.",
                                "The process of formatting a hard drive."
                        },
                        0
                )
        );

        questionList.add(
                new Question(
                        "What does maintainable code mean?",
                        "Professional & Development Practices",
                        new String[]{
                                "Code that is easy to understand, modify, test and maintain over time.",
                                "Code that can never be changed.",
                                "Code written entirely on one line.",
                                "Code that does not require testing."
                        },
                        0
                )
        );
    }



    // Timer


    private void startTimer() {

        countDownTimer = new CountDownTimer(TEST_DURATION, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;

                String timeText = String.format(
                        "◷  %02d:%02d Remaining",
                        minutes,
                        seconds
                );

                tvTimer.setText(timeText);
            }


            @Override
            public void onFinish() {

                tvTimer.setText("◷  00:00 Remaining");

                saveCurrentAnswer();

                calculateResults();
            }

        }.start();
    }



    // Load question
    private void loadQuestion() {

        Question question = questionList.get(currentQuestion);

        tvQuestionNumber.setText(
                "Question " + (currentQuestion + 1)
                        + " of " + questionList.size()
        );

        tvQuestion.setText(question.getQuestionText());

        questionProgress.setProgress(currentQuestion + 1);

        answerGroup.removeAllViews();

        String[] answers = question.getAnswers();

        for (int i = 0; i < answers.length; i++) {

            RadioButton radioButton = new RadioButton(this);

            radioButton.setId(View.generateViewId());

            radioButton.setText(answers[i]);

            radioButton.setTextSize(14);

            radioButton.setTextColor(
                    getResources().getColor(
                            android.R.color.darker_gray
                    )
            );

            radioButton.setPadding(
                    8,
                    12,
                    8,
                    12
            );

            answerGroup.addView(radioButton);
        }


        // Restore the user's previous answer
        int previousAnswer = selectedAnswers[currentQuestion];

        if (previousAnswer != -1
                && previousAnswer < answerGroup.getChildCount()) {

            RadioButton selectedButton =
                    (RadioButton) answerGroup.getChildAt(previousAnswer);

            selectedButton.setChecked(true);
        }


        // Disable previous on the first question
        btnPrevious.setEnabled(currentQuestion > 0);


        // Change next to Finish on the final question
        if (currentQuestion == questionList.size() - 1) {
            btnNext.setText("Finish");
        } else {
            btnNext.setText("Next");
        }
    }



    // Savw current answer

    private void saveCurrentAnswer() {

        int checkedId = answerGroup.getCheckedRadioButtonId();

        if (checkedId == -1) {
            return;
        }

        for (int i = 0; i < answerGroup.getChildCount(); i++) {

            View child = answerGroup.getChildAt(i);

            if (child.getId() == checkedId) {

                selectedAnswers[currentQuestion] = i;

                break;
            }
        }
    }



    // CALCULATE RESULTS

    private void calculateResults() {

        int correctCount = 0;

        // Store scores for each category
        LinkedHashMap<String, Integer> categoryScores =
                new LinkedHashMap<>();

        // Initialize all categories
        for (Question question : questionList) {

            if (!categoryScores.containsKey(question.getCategory())) {
                categoryScores.put(question.getCategory(), 0);
            }
        }


        // Calculate correct answers
        for (int i = 0; i < questionList.size(); i++) {

            Question question = questionList.get(i);

            if (selectedAnswers[i] == question.getCorrectAnswer()) {

                correctCount++;

                String category = question.getCategory();

                categoryScores.put(
                        category,
                        categoryScores.get(category) + 1
                );
            }
        }


        int incorrectCount = questionList.size() - correctCount;

        int percentage =
                (int) (((double) correctCount / questionList.size()) * 100);


        // Find strengths and areas for improvement
        StringBuilder strengths = new StringBuilder();
        StringBuilder areasForImprovement = new StringBuilder();

        String strongestCategory = "";
        String weakestCategory = "";

        int highestScore = -1;
        int lowestScore = Integer.MAX_VALUE;


        for (Map.Entry<String, Integer> entry
                : categoryScores.entrySet()) {

            String category = entry.getKey();
            int score = entry.getValue();


            // Find strongest category
            if (score > highestScore) {

                highestScore = score;
                strongestCategory = category;
            }


            // Find weakest category
            if (score < lowestScore) {

                lowestScore = score;
                weakestCategory = category;
            }


            // Add strengths
            if (score >= 4) {

                if (strengths.length() > 0) {
                    strengths.append("\n");
                }

                strengths.append(category);
            }


            // Add areas for improvement
            if (score <= 2) {

                if (areasForImprovement.length() > 0) {
                    areasForImprovement.append("\n");
                }

                areasForImprovement.append(category);
            }
        }


        if (strengths.length() == 0) {
            strengths.append(strongestCategory);
        }


        if (areasForImprovement.length() == 0) {
            areasForImprovement.append(weakestCategory);
        }


        // Time taken
        long timeTakenMillis =
                System.currentTimeMillis() - testStartTime;

        // Completion time
        long completionTimeMillis =
                System.currentTimeMillis();


        // Send results to assessment complete activity
        Intent intent = new Intent(
                BaselineTestActivity.this,
                AssessmentCompleteActivity.class
        );

        intent.putExtra("correctCount", correctCount);
        intent.putExtra("incorrectCount", incorrectCount);
        intent.putExtra("percentage", percentage);

        intent.putExtra(
                "strengths",
                strengths.toString()
        );

        intent.putExtra(
                "areasImprovement",
                areasForImprovement.toString()
        );

        intent.putExtra(
                "timeTakenMillis",
                timeTakenMillis
        );

        intent.putExtra(
                "completionTimeMillis",
                completionTimeMillis
        );


        startActivity(intent);

        finish();
    }



    // CLEAN UP TIMER
    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        super.onDestroy();
    }


    // QUESTION CLASS
    private static class Question {

        private final String questionText;
        private final String category;
        private final String[] answers;
        private final int correctAnswer;


        public Question(
                String questionText,
                String category,
                String[] answers,
                int correctAnswer
        ) {

            this.questionText = questionText;
            this.category = category;
            this.answers = answers;
            this.correctAnswer = correctAnswer;
        }


        public String getQuestionText() {
            return questionText;
        }


        public String getCategory() {
            return category;
        }


        public String[] getAnswers() {
            return answers;
        }


        public int getCorrectAnswer() {
            return correctAnswer;
        }
    }
}