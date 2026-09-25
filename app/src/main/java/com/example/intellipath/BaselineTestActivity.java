package com.example.intellipath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Intent;
import android.os.Bundle;

import com.example.intellipath.data.AssessmentRepository;
import kotlin.Unit;

public class BaselineTestActivity extends AppCompatActivity {

    private android.widget.TextView tvQuestionNumber;
    private android.widget.TextView tvTimer;
    private android.widget.TextView tvQuestion;
    private android.widget.ProgressBar questionProgress;
    private android.widget.RadioGroup answerGroup;
    private android.widget.Button btnPrevious;
    private android.widget.Button btnNext;

    private final List<Question> questionList = new ArrayList<>();
    private final List<Integer> selectedAnswers = new ArrayList<>();

    private int currentQuestion = 0;

    private static final long TEST_DURATION = 60 * 60 * 1000L;

    private CountDownTimer countDownTimer;
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

        createQuestions();

        for (int i = 0; i < questionList.size(); i++) {
            selectedAnswers.add(-1);
        }

        questionProgress.setMax(questionList.size());

        testStartTime = System.currentTimeMillis();

        loadQuestion();
        startTimer();

        btnPrevious.setOnClickListener(v -> {
            saveCurrentAnswer();

            if (currentQuestion > 0) {
                currentQuestion--;
                loadQuestion();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (!saveCurrentAnswer()) {
                Toast.makeText(
                        BaselineTestActivity.this,
                        "Please select an answer before continuing.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (currentQuestion < questionList.size() - 1) {
                currentQuestion++;
                loadQuestion();
            } else {
                calculateResults();
            }
        });
    }

    private void createQuestions() {

        // ============================================================
        // 1. GIT & VERSION CONTROL - 12 QUESTIONS
        // ============================================================

        addQuestion(
                "Git & Version Control",
                "What is the main purpose of Git?",
                1,
                "Execute Java programs",
                "Manage and track source-code changes",
                "Host applications automatically",
                "Convert source code into machine code"
        );

        addQuestion(
                "Git & Version Control",
                "What does `git status` show?",
                0,
                "The current state of the working tree and staging area",
                "The contents of every previous commit",
                "The remote server's operating system",
                "The number of developers on the repository"
        );

        addQuestion(
                "Git & Version Control",
                "What is the purpose of `git add Main.java`?",
                2,
                "It creates a new branch",
                "It sends Main.java to GitHub",
                "It stages Main.java for the next commit",
                "It permanently commits Main.java"
        );

        addQuestion(
                "Git & Version Control",
                "What is the purpose of a Git commit?",
                1,
                "To delete the current branch",
                "To record a set of changes in the repository history",
                "To download the latest project version",
                "To create a database backup"
        );

        addQuestion(
                "Git & Version Control",
                "Which command is commonly used to view previous commits?",
                2,
                "`git branch`",
                "`git status`",
                "`git log`",
                "`git fetch`"
        );

        addQuestion(
                "Git & Version Control",
                "You are developing a new login feature and do not want to make changes directly on the development branch. What should you normally create?",
                1,
                "A database table",
                "A separate feature branch",
                "A new repository account",
                "A compiled executable"
        );

        addQuestion(
                "Git & Version Control",
                "You run `git branch` and see the following output:\n\n* feature-login\n  dev\n  main\n\nWhich branch are you currently on?",
                2,
                "main",
                "dev",
                "feature-login",
                "All three branches"
        );

        addQuestion(
                "Git & Version Control",
                "The `dev` branch is protected and requires code review before changes are merged. You have finished a feature on your own branch. What should you normally do?",
                2,
                "Push directly to protected dev",
                "Delete the feature branch",
                "Push the branch and create a pull request",
                "Copy the code into a new repository"
        );

        addQuestion(
                "Git & Version Control",
                "You make a commit locally but have not pushed it. Where does that commit currently exist?",
                1,
                "Only on GitHub",
                "In your local Git repository",
                "Only inside the IDE",
                "Only inside the remote branch"
        );

        addQuestion(
                "Git & Version Control",
                "Two developers modify the same lines of a file, and Git cannot automatically combine their changes. What has occurred?",
                0,
                "A merge conflict",
                "A successful merge",
                "A detached commit",
                "A clean checkout"
        );

        addQuestion(
                "Git & Version Control",
                "What does `git clone` normally do?",
                1,
                "Deletes a remote repository",
                "Creates a local copy of a remote repository",
                "Merges two local branches",
                "Stages modified files"
        );

        addQuestion(
                "Git & Version Control",
                "Why would a team create a pull request into a protected `dev` branch?",
                1,
                "To bypass code review",
                "To allow changes to be reviewed before merging",
                "To remove the branch from GitHub",
                "To convert Java files into XML"
        );

        // ============================================================
        // 2. DATA STRUCTURES & ALGORITHMS - 13 QUESTIONS
        // ============================================================

        addQuestion(
                "Data Structures & Algorithms",
                "What is the main purpose of an array?",
                1,
                "Store values of different types automatically",
                "Store multiple values using indexed positions",
                "Sort values whenever they are inserted",
                "Prevent values from being accessed directly"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "What will this Java code print?\n\nint[] numbers = {4, 7, 2, 9};\nSystem.out.println(numbers[2]);",
                2,
                "4",
                "7",
                "2",
                "9"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "You need to find whether the number 25 exists in an unsorted array. Which approach directly checks each element until a match is found?",
                0,
                "Linear search",
                "Binary search",
                "Bubble sort",
                "Recursion"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "What will the following code print?\n\nint[] numbers = {5, 3, 8, 1};\n\nfor (int i = 0; i < numbers.length; i++) {\n    if (numbers[i] > 4) {\n        System.out.print(numbers[i] + \" \");\n    }\n}",
                2,
                "5 3 8",
                "3 1",
                "5 8",
                "8 1"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "A sorted array contains 2, 5, 8, 12, 16, 21, 30. You need to determine whether 16 exists. Which search repeatedly divides the search range in half?",
                1,
                "Linear search",
                "Binary search",
                "Bubble sort",
                "Sequential insertion"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "What is the final state of the array after one complete pass of bubble sort in ascending order?\n\n{5, 2, 4, 1}",
                0,
                "{2, 4, 1, 5}",
                "{2, 5, 1, 4}",
                "{1, 2, 4, 5}",
                "{5, 4, 2, 1}"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "What will this method return when called with 3?\n\nstatic int factorial(int n) {\n    if (n == 1) {\n        return 1;\n    }\n    return n * factorial(n - 1);\n}",
                1,
                "3",
                "6",
                "9",
                "12"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "What is printed by countDown(4)?\n\nstatic void countDown(int n) {\n    if (n == 0) {\n        return;\n    }\n    System.out.print(n + \" \");\n    countDown(n - 1);\n}",
                2,
                "0 1 2 3 4",
                "4 3 2 1 0",
                "4 3 2 1",
                "1 2 3 4"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "You have an array of 1,000 unsorted values and need to find the largest value without sorting the entire array. What is the most direct approach?",
                0,
                "Check each value while keeping track of the largest",
                "Compare only the first and last values",
                "Use binary search on the unsorted values",
                "Reverse the array before checking it"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "What is the output?\n\nint[] nums = {3, 6, 9, 12};\nint sum = 0;\n\nfor (int i = 0; i < nums.length; i++) {\n    sum += nums[i];\n}\n\nSystem.out.println(sum);",
                3,
                "21",
                "27",
                "30",
                "36"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "Ignoring the cost of printing, how does the number of iterations in this loop grow as n increases?\n\nfor (int i = 0; i < n; i++) {\n    System.out.println(i);\n}",
                1,
                "It stays constant",
                "It grows proportionally with n",
                "It grows by repeatedly halving n",
                "It grows proportionally with n²"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "An array has length 5. Which index is invalid?",
                3,
                "0",
                "3",
                "4",
                "5"
        );

        addQuestion(
                "Data Structures & Algorithms",
                "A football application stores the jersey numbers of 20 players in an unsorted array. You need to check whether jersey number 10 is present. Which approach is appropriate?",
                1,
                "Binary search without changing the array",
                "Linear search through the stored values",
                "Access index 10 directly",
                "Use bubble sort without checking the values"
        );

        // ============================================================
        // 3. DATABASES & SQL - 13 QUESTIONS
        // ============================================================

        addQuestion(
                "Databases & SQL",
                "Which SQL statement retrieves all columns from a table called Players?",
                2,
                "GET * FROM Players;",
                "SELECT ALL Players;",
                "SELECT * FROM Players;",
                "SHOW Players *;"
        );

        addQuestion(
                "Databases & SQL",
                "What will this query return?\n\nSELECT name, position\nFROM Players\nWHERE position = 'Midfielder';",
                1,
                "Every player except midfielders",
                "Only players whose position is Midfielder",
                "Only the position column for every player",
                "Every player whose name contains Midfielder"
        );

        addQuestion(
                "Databases & SQL",
                "A Students table contains the following records: Thabo has 72, Kabelo has 48, Musa has 81, and Sipho has 65. What does this query return?\n\nSELECT name\nFROM Students\nWHERE mark >= 65;",
                0,
                "Thabo, Musa and Sipho",
                "Kabelo and Sipho",
                "Thabo and Kabelo",
                "Musa only"
        );

        addQuestion(
                "Databases & SQL",
                "You want players ordered from the highest salary to the lowest salary. Which query is correct?",
                3,
                "SELECT * FROM Players ORDER BY salary ASC;",
                "SELECT * FROM Players SORT BY salary DESC;",
                "SELECT * FROM Players WHERE salary DESC;",
                "SELECT * FROM Players ORDER BY salary DESC;"
        );

        addQuestion(
                "Databases & SQL",
                "What does this query calculate?\n\nSELECT COUNT(*)\nFROM Players;",
                0,
                "The total number of rows",
                "The sum of all player values",
                "The highest player value",
                "The number of different columns"
        );

        addQuestion(
                "Databases & SQL",
                "A table called Orders contains an amount column. Which query calculates the total value of all orders?",
                1,
                "SELECT COUNT(amount) FROM Orders;",
                "SELECT SUM(amount) FROM Orders;",
                "SELECT TOTAL(amount) FROM Orders;",
                "SELECT ADD(amount) FROM Orders;"
        );

        addQuestion(
                "Databases & SQL",
                "You want to find the average mark for all students. Which SQL function should you use?",
                2,
                "SUM()",
                "COUNT()",
                "AVG()",
                "MAX()"
        );

        addQuestion(
                "Databases & SQL",
                "What does this query do?\n\nSELECT position, COUNT(*)\nFROM Players\nGROUP BY position;",
                0,
                "Counts players for each position",
                "Counts every column in the table",
                "Sorts players according to position",
                "Removes duplicate player records"
        );

        addQuestion(
                "Databases & SQL",
                "You want to show only positions that have more than 3 players. Which condition belongs after GROUP BY?",
                2,
                "WHERE COUNT(*) > 3",
                "ORDER BY COUNT(*) > 3",
                "HAVING COUNT(*) > 3",
                "GROUP BY COUNT(*) > 3"
        );

        addQuestion(
                "Databases & SQL",
                "A Players table has player_id, name and team_id. A Teams table has team_id and team_name. Which join returns players with matching team names while excluding players without a matching team?",
                0,
                "INNER JOIN",
                "LEFT JOIN",
                "FULL JOIN",
                "CROSS JOIN"
        );

        addQuestion(
                "Databases & SQL",
                "What does this query return?\n\nSELECT name\nFROM Players\nWHERE salary > 10000\n  AND position = 'Defender';",
                3,
                "Defenders earning exactly 10,000",
                "All players earning more than 10,000",
                "Players who are defenders or earn more than 10,000",
                "Defenders whose salary is greater than 10,000"
        );

        addQuestion(
                "Databases & SQL",
                "In this query, which players are considered before the averages are calculated?\n\nSELECT position, AVG(salary)\nFROM Players\nWHERE salary > 5000\nGROUP BY position;",
                1,
                "Players with salary below 5,000",
                "Players with salary above 5,000",
                "All players, regardless of salary",
                "Only the position with the highest salary"
        );

        addQuestion(
                "Databases & SQL",
                "You need the names of students whose marks are higher than the average mark of the entire class. Which approach is appropriate?",
                2,
                "Compare every mark with COUNT(mark)",
                "Sort the names alphabetically first",
                "Compare each mark with a subquery that calculates AVG(mark)",
                "Use GROUP BY name without calculating an average"
        );

        // ============================================================
        // 4. SOFTWARE ENGINEERING - 12 QUESTIONS
        // ============================================================

        addQuestion(
                "Software Engineering",
                "What is the main purpose of requirements analysis?",
                1,
                "Choose the programming language",
                "Identify what the system should do",
                "Write the database queries",
                "Design the user interface"
        );

        addQuestion(
                "Software Engineering",
                "A client says, \"Users must be able to reset their password.\" What type of requirement is this?",
                0,
                "Functional requirement",
                "Performance requirement",
                "Security algorithm",
                "Hardware requirement"
        );

        addQuestion(
                "Software Engineering",
                "Which development approach divides a project into smaller iterations that can be developed and reviewed separately?",
                2,
                "Waterfall",
                "Big Bang",
                "Agile",
                "V-Model"
        );

        addQuestion(
                "Software Engineering",
                "A developer changes one method and then runs existing tests to check that previously working functionality has not broken. What type of testing is this?",
                1,
                "Acceptance testing",
                "Regression testing",
                "Load testing",
                "Usability testing"
        );

        addQuestion(
                "Software Engineering",
                "A banking application is planned in sequence, with requirements, design and development stages completed one after another. Which model does this describe?",
                3,
                "Agile",
                "Scrum",
                "Prototype",
                "Waterfall"
        );

        addQuestion(
                "Software Engineering",
                "In UML, what does an actor normally represent in a use-case diagram?",
                1,
                "A database table",
                "A person or external system interacting with the system",
                "A method inside a class",
                "A process running on the server"
        );

        addQuestion(
                "Software Engineering",
                "Which relationship is most appropriate when one class is a more specific version of another class?",
                0,
                "Inheritance",
                "Aggregation",
                "Association",
                "Dependency"
        );

        addQuestion(
                "Software Engineering",
                "A Vehicle class contains startEngine(). A Car class extends Vehicle. What can Car normally do?",
                2,
                "Access only private variables from Vehicle",
                "Delete the Vehicle class",
                "Inherit accessible behaviour from Vehicle",
                "Prevent Vehicle from being instantiated"
        );

        addQuestion(
                "Software Engineering",
                "Why is version control useful when multiple developers work on the same project?",
                2,
                "It automatically writes all application code",
                "It removes the need for testing",
                "It records changes and helps developers collaborate",
                "It prevents developers from creating separate branches"
        );

        addQuestion(
                "Software Engineering",
                "A team finds the same block of code in five different methods. Which principle suggests reducing this duplication?",
                0,
                "DRY",
                "FIFO",
                "ACID",
                "MVC"
        );

        addQuestion(
                "Software Engineering",
                "A team wants to create a simple working version of an application to gather feedback before building the complete system. What are they creating?",
                1,
                "Compiler",
                "Prototype",
                "Database index",
                "Test case"
        );

        addQuestion(
                "Software Engineering",
                "A method handles database access, user-interface updates and business calculations. What is the main concern?",
                1,
                "The method has too few variables",
                "The method combines several responsibilities",
                "The database must be deleted",
                "The application cannot use inheritance"
        );

        // ============================================================
        // 5. TESTING & DEBUGGING - 12 QUESTIONS
        // ============================================================

        addQuestion(
                "Testing & Debugging",
                "What is the main purpose of software testing?",
                1,
                "To guarantee the software has no bugs",
                "To find defects and check whether the system behaves as expected",
                "To replace the development process",
                "To increase the amount of source code"
        );

        addQuestion(
                "Testing & Debugging",
                "A developer tests a single method that calculates a player's average score. What type of testing is most appropriate?",
                0,
                "Unit testing",
                "System testing",
                "Acceptance testing",
                "Load testing"
        );

        addQuestion(
                "Testing & Debugging",
                "What will this Java code print?\n\nint x = 10;\nint y = 0;\nSystem.out.println(x / y);",
                3,
                "0",
                "10",
                "Infinity",
                "It throws an exception"
        );

        addQuestion(
                "Testing & Debugging",
                "A program works correctly for positive numbers but crashes when the user enters 0. What should the developer do first?",
                1,
                "Remove the input",
                "Investigate the code path triggered by 0",
                "Rewrite the entire application",
                "Increase the computer's RAM"
        );

        addQuestion(
                "Testing & Debugging",
                "Which statement correctly describes a breakpoint when debugging?",
                2,
                "It permanently stops a program from running",
                "It deletes the line where it is placed",
                "It pauses execution so the program state can be inspected",
                "It automatically fixes the error"
        );

        addQuestion(
                "Testing & Debugging",
                "Consider:\n\nint[] numbers = {10, 20, 30};\nSystem.out.println(numbers[3]);\n\nWhat is the likely result?",
                3,
                "30",
                "0",
                "10",
                "An ArrayIndexOutOfBoundsException"
        );

        addQuestion(
                "Testing & Debugging",
                "A login test checks that a valid username and password allow the user to enter the application. What is being tested?",
                0,
                "Expected valid behaviour",
                "Database storage capacity",
                "Network bandwidth",
                "Source-code formatting"
        );

        addQuestion(
                "Testing & Debugging",
                "Which situation is most suitable for negative testing?",
                1,
                "Entering a valid email address",
                "Entering an invalid email address",
                "Logging in with the correct password",
                "Saving a correctly completed form"
        );

        addQuestion(
                "Testing & Debugging",
                "A test initially passes. A developer changes authentication code, and the same test now fails. What might this indicate?",
                0,
                "A regression",
                "A syntax comment",
                "A successful deployment",
                "A faster algorithm"
        );

        addQuestion(
                "Testing & Debugging",
                "What is the output?\n\nint mark = 45;\n\nif (mark >= 50) {\n    System.out.println(\"Pass\");\n} else {\n    System.out.println(\"Fail\");\n}",
                1,
                "Pass",
                "Fail",
                "45",
                "No output"
        );

        addQuestion(
                "Testing & Debugging",
                "A NullPointerException occurs when a method is called using a reference that has not been assigned an object. What should the developer investigate?",
                0,
                "Whether the reference is null before use",
                "Whether the monitor resolution is correct",
                "Whether the database has enough rows",
                "Whether the program uses enough classes"
        );

        addQuestion(
                "Testing & Debugging",
                "A tester reports that clicking Submit sometimes produces no response, but the developer cannot reproduce it immediately. What information would be most useful?",
                2,
                "The developer's favourite programming language",
                "The application's logo",
                "The exact steps, inputs and conditions when the problem occurs",
                "The number of files in an unrelated project"
        );

        // ============================================================
        // 6. WEB & HTTP - 12 QUESTIONS
        // ============================================================

        addQuestion(
                "Web & HTTP",
                "What is HTTP primarily used for?",
                1,
                "Managing files on a local computer",
                "Communicating between web clients and servers",
                "Compiling Java applications",
                "Storing relational database records"
        );

        addQuestion(
                "Web & HTTP",
                "A user enters a website address into a browser. Which component typically sends the initial HTTP request?",
                0,
                "Web browser",
                "Database",
                "Compiler",
                "Operating system kernel"
        );

        addQuestion(
                "Web & HTTP",
                "What does an HTTP GET request normally do?",
                1,
                "Deletes a resource",
                "Retrieves data from a server",
                "Updates a database record",
                "Creates a new server"
        );

        addQuestion(
                "Web & HTTP",
                "A client sends POST /users with user information in the request body. What is the most likely purpose?",
                2,
                "Retrieve an existing user",
                "Remove a user",
                "Create or submit user data",
                "Test the server's DNS configuration"
        );

        addQuestion(
                "Web & HTTP",
                "A server responds with HTTP/1.1 404 Not Found. What does this normally indicate?",
                1,
                "The request succeeded",
                "The requested resource could not be found",
                "The server created a new resource",
                "The client has been permanently authenticated"
        );

        addQuestion(
                "Web & HTTP",
                "Which HTTP status code normally indicates that a request was successful?",
                0,
                "200",
                "301",
                "404",
                "500"
        );

        addQuestion(
                "Web & HTTP",
                "Which HTTP status-code category is associated with redirection?",
                2,
                "1xx",
                "2xx",
                "3xx",
                "5xx"
        );

        addQuestion(
                "Web & HTTP",
                "What is the main difference between HTTP and HTTPS?",
                0,
                "HTTPS adds encrypted communication using TLS",
                "HTTP can only transfer images",
                "HTTPS removes the need for a web server",
                "HTTP automatically encrypts every request"
        );

        addQuestion(
                "Web & HTTP",
                "In GET /players?id=10, what does id=10 represent?",
                1,
                "A response status",
                "A query parameter",
                "A request method",
                "A server header"
        );

        addQuestion(
                "Web & HTTP",
                "A web application receives HTTP/1.1 500 Internal Server Error. Which situation best matches this response?",
                2,
                "The requested page was permanently moved",
                "The client successfully logged in",
                "The server encountered an unexpected problem",
                "The requested resource was found"
        );

        addQuestion(
                "Web & HTTP",
                "Which HTTP method is commonly used when partially modifying an existing resource?",
                2,
                "GET",
                "POST",
                "PATCH",
                "HEAD"
        );

        addQuestion(
                "Web & HTTP",
                "A JavaScript frontend sends a request to an API, but the browser blocks access because the API does not allow requests from that website's origin. What browser security mechanism is involved?",
                0,
                "CORS",
                "DNS",
                "DHCP",
                "FTP"
        );

        // ============================================================
        // 7. NETWORKING & CLOUD - 13 QUESTIONS
        // ============================================================

        addQuestion(
                "Networking & Cloud",
                "What is the main purpose of an IP address?",
                0,
                "Identify a device or network interface on a network",
                "Store a website's HTML content",
                "Encrypt every network packet",
                "Determine a user's password"
        );

        addQuestion(
                "Networking & Cloud",
                "Which protocol is commonly used to translate a domain name such as example.com into an IP address?",
                1,
                "HTTP",
                "DNS",
                "FTP",
                "SSH"
        );

        addQuestion(
                "Networking & Cloud",
                "A browser needs the IP address for www.example.com before connecting to the server. Which process handles this?",
                0,
                "DNS resolution",
                "Packet encryption",
                "File compression",
                "Port forwarding"
        );

        addQuestion(
                "Networking & Cloud",
                "Which protocol automatically assigns IP addresses and other network configuration information to devices?",
                2,
                "DNS",
                "HTTP",
                "DHCP",
                "TCP"
        );

        addQuestion(
                "Networking & Cloud",
                "What is the main role of TCP?",
                1,
                "Translate domain names",
                "Provide reliable, ordered delivery of data",
                "Assign IP addresses to devices",
                "Encrypt website traffic"
        );

        addQuestion(
                "Networking & Cloud",
                "A packet is sent across a network, but the receiver does not get it. Which TCP feature helps detect missing data and arrange for retransmission?",
                0,
                "Acknowledgements and retransmission",
                "DNS caching",
                "IP addressing",
                "Port scanning"
        );

        addQuestion(
                "Networking & Cloud",
                "A web server accepts HTTP traffic on port 80 and HTTPS traffic on port 443. What does a port number help identify?",
                1,
                "The physical location of the server",
                "The network service being accessed",
                "The user's operating system",
                "The size of the transmitted file"
        );

        addQuestion(
                "Networking & Cloud",
                "A company moves its application from a physical office server to a cloud provider. Which cloud characteristic allows resources to be obtained without owning the physical infrastructure?",
                1,
                "Virtualisation only",
                "On-demand resource provisioning",
                "Local file storage",
                "Manual hardware assembly"
        );

        addQuestion(
                "Networking & Cloud",
                "A website experiences a sudden increase in users and automatically adds server instances. What cloud capability is being demonstrated?",
                1,
                "Encryption",
                "Scalability",
                "DNS resolution",
                "Data normalisation"
        );

        addQuestion(
                "Networking & Cloud",
                "What is the main difference between scaling vertically and scaling horizontally?",
                0,
                "Vertical adds resources to a machine; horizontal adds machines",
                "Vertical adds machines; horizontal removes machines",
                "Vertical changes the database; horizontal changes the network",
                "Vertical uses DNS; horizontal uses HTTP"
        );

        addQuestion(
                "Networking & Cloud",
                "A company wants application data to remain available if one database server fails. Which approach can help provide this?",
                0,
                "Replication across multiple instances",
                "Removing database backups",
                "Using a single server only",
                "Disabling network connections"
        );

        addQuestion(
                "Networking & Cloud",
                "A developer connects securely to a remote Linux server from a terminal. Which protocol is commonly used?",
                1,
                "FTP",
                "SSH",
                "DHCP",
                "DNS"
        );

        addQuestion(
                "Networking & Cloud",
                "An application is deployed using containers. What is one major benefit of containerisation?",
                1,
                "It removes the need for an operating system entirely",
                "It packages an application with its required environment",
                "It guarantees unlimited computing resources",
                "It prevents all application bugs"
        );

        // ============================================================
        // 8. PROFESSIONAL & DEVELOPMENT PRACTICES - 13 QUESTIONS
        // ============================================================

        addQuestion(
                "Professional & Development Practices",
                "A developer is given a task but is unsure what the client expects. What should they do first?",
                1,
                "Start coding immediately",
                "Clarify the requirements",
                "Deploy a prototype",
                "Change the database design"
        );

        addQuestion(
                "Professional & Development Practices",
                "A team uses Git and wants a new feature isolated from the main development branch until reviewed. What should the developer use?",
                0,
                "A separate feature branch",
                "A database trigger",
                "A production server",
                "A compiled JAR file"
        );

        addQuestion(
                "Professional & Development Practices",
                "Why are code reviews useful?",
                2,
                "They remove the need for testing",
                "They prevent developers from using version control",
                "They allow another developer to identify issues and suggest improvements",
                "They automatically rewrite inefficient code"
        );

        addQuestion(
                "Professional & Development Practices",
                "A developer realises a task will take longer than expected. What is the most appropriate action?",
                1,
                "Say nothing until the deadline passes",
                "Communicate the issue and discuss the revised timeline",
                "Submit incomplete work without explanation",
                "Delete the task from the project board"
        );

        addQuestion(
                "Professional & Development Practices",
                "Which practice makes code easier for another developer to understand?",
                0,
                "Using meaningful names and consistent formatting",
                "Putting every operation into one method",
                "Removing all comments and documentation",
                "Using short variable names everywhere"
        );

        addQuestion(
                "Professional & Development Practices",
                "Two developers modify the same section of code. What should they do before combining the work?",
                1,
                "Delete one developer's branch",
                "Compare the changes and resolve any conflicts",
                "Ignore the differences and push both versions",
                "Create a new programming language"
        );

        addQuestion(
                "Professional & Development Practices",
                "What is the purpose of documentation in a software project?",
                1,
                "To replace the source code",
                "To explain relevant system information and usage",
                "To prevent future developers from changing the system",
                "To guarantee that the application has no bugs"
        );

        addQuestion(
                "Professional & Development Practices",
                "A developer accidentally commits a file containing a database password to a public repository. What is the immediate security concern?",
                0,
                "The password may now be exposed",
                "The repository will automatically delete itself",
                "The programming language has become invalid",
                "The database will stop accepting queries"
        );

        addQuestion(
                "Professional & Development Practices",
                "Which practice helps prevent sensitive information such as API keys from being committed to source control?",
                1,
                "Store secrets directly in every Java class",
                "Use secure secret management and exclude local secret files from commits",
                "Rename the API key variable",
                "Put the password inside a comment"
        );

        addQuestion(
                "Professional & Development Practices",
                "A developer receives feedback during a code review that a method is difficult to understand. What is a reasonable response?",
                1,
                "Ignore the feedback because the code runs",
                "Discuss the feedback and improve the code where appropriate",
                "Delete the entire project",
                "Remove the reviewer from the repository"
        );

        addQuestion(
                "Professional & Development Practices",
                "A project manager asks for the current progress of a development task. Which information is most useful?",
                1,
                "The developer's preferred editor",
                "The current status, completed work, and remaining work",
                "The number of programming languages the developer knows",
                "The developer's computer specifications"
        );

        addQuestion(
                "Professional & Development Practices",
                "A developer finishes implementing a feature. Before considering the task complete, what should normally happen?",
                0,
                "The code should be tested and reviewed as required by the project",
                "The source code should immediately be deleted",
                "The feature should move directly to production without testing",
                "The developer should remove the feature branch immediately"
        );

        addQuestion(
                "Professional & Development Practices",
                "A developer notices that they will miss an agreed deadline because another task is blocking their progress. What is the most professional response?",
                2,
                "Hide the delay until someone notices",
                "Continue silently and provide no update",
                "Communicate the blocker early and discuss how to proceed",
                "Mark the task complete before it is finished"
        );
    }

    private void addQuestion(
            String category,
            String question,
            int correctAnswer,
            String... answers
    ) {
        questionList.add(
                new Question(
                        category,
                        question,
                        answers,
                        correctAnswer
                )
        );
    }

    private void startTimer() {

        countDownTimer = new CountDownTimer(TEST_DURATION, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                long totalSeconds = millisUntilFinished / 1000;
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;

                tvTimer.setText(
                        String.format(
                                "%02d:%02d",
                                minutes,
                                seconds
                        )
                );
            }

            @Override
            public void onFinish() {

                tvTimer.setText("00:00");

                Toast.makeText(
                        BaselineTestActivity.this,
                        "Time is up. Your assessment will now be submitted.",
                        Toast.LENGTH_LONG
                ).show();

                saveCurrentAnswer();
                calculateResults();
            }
        }.start();
    }

    private void loadQuestion() {

        if (currentQuestion < 0 || currentQuestion >= questionList.size()) {
            return;
        }

        Question question = questionList.get(currentQuestion);

        tvQuestionNumber.setText(
                "Question " + (currentQuestion + 1) +
                        " of " + questionList.size()
        );

        tvQuestion.setText(question.getQuestion());

        questionProgress.setProgress(currentQuestion + 1);

        answerGroup.removeAllViews();

        String[] answers = question.getAnswers();

        for (int i = 0; i < answers.length; i++) {

            RadioButton radioButton = new RadioButton(this);

            radioButton.setText(answers[i]);
            radioButton.setTextSize(16);
            radioButton.setPadding(8, 20, 8, 20);

            radioButton.setId(View.generateViewId());

            final int answerIndex = i;

            radioButton.setOnClickListener(v -> {
                selectedAnswers.set(currentQuestion, answerIndex);
            });

            answerGroup.addView(radioButton);

            if (selectedAnswers.get(currentQuestion) == i) {
                radioButton.setChecked(true);
            }
        }

        if (currentQuestion == 0) {
            btnPrevious.setVisibility(View.INVISIBLE);
        } else {
            btnPrevious.setVisibility(View.VISIBLE);
        }

        if (currentQuestion == questionList.size() - 1) {
            btnNext.setText("Submit");
        } else {
            btnNext.setText("Next");
        }
    }

    private boolean saveCurrentAnswer() {

        int selectedId = answerGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            return selectedAnswers.get(currentQuestion) != -1;
        }

        for (int i = 0; i < answerGroup.getChildCount(); i++) {

            View child = answerGroup.getChildAt(i);

            if (child instanceof RadioButton) {

                RadioButton radioButton = (RadioButton) child;

                if (radioButton.getId() == selectedId) {
                    selectedAnswers.set(currentQuestion, i);
                    return true;
                }
            }
        }

        return false;
    }

    private void calculateResults() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long timeTakenMillis =
                System.currentTimeMillis() - testStartTime;

        int correctCount = 0;

        Map<String, Integer> categoryCorrect = new HashMap<>();
        Map<String, Integer> categoryTotal = new HashMap<>();

        for (int i = 0; i < questionList.size(); i++) {

            Question question = questionList.get(i);

            String category = question.getCategory();

            categoryTotal.put(
                    category,
                    categoryTotal.getOrDefault(category, 0) + 1
            );

            int selectedAnswer = selectedAnswers.get(i);

            if (selectedAnswer == question.getCorrectAnswer()) {

                correctCount++;

                categoryCorrect.put(
                        category,
                        categoryCorrect.getOrDefault(category, 0) + 1
                );
            }
        }

        int incorrectCount =
                questionList.size() - correctCount;

        int percentage =
                Math.round(
                        (correctCount * 100f) / questionList.size()
                );

        String strongestCategory = "";
        String weakestCategory = "";

        double strongestScore = -1;
        double weakestScore = Double.MAX_VALUE;

        for (String category : categoryTotal.keySet()) {

            int total = categoryTotal.get(category);
            int correct = categoryCorrect.getOrDefault(category, 0);

            double score = (correct * 100.0) / total;

            if (score > strongestScore) {
                strongestScore = score;
                strongestCategory = category;
            }

            if (score < weakestScore) {
                weakestScore = score;
                weakestCategory = category;
            }
        }

        String strengths =
                strongestCategory +
                        " (" +
                        Math.round(strongestScore) +
                        "%)";

        String areasImprovement =
                weakestCategory +
                        " (" +
                        Math.round(weakestScore) +
                        "%)";

// Mark the Baseline Assessment as completed
        SharedPreferences preferences =
                getSharedPreferences("IntelliPathPrefs", MODE_PRIVATE);

        preferences.edit()
                .putBoolean("baseline_completed", true)
                .apply();

        Intent intent = new Intent(
                BaselineTestActivity.this,
                AssessmentCompleteActivity.class
        );


        intent.putExtra(
                "correctCount",
                correctCount
        );

        intent.putExtra(
                "incorrectCount",
                incorrectCount
        );

        intent.putExtra(
                "percentage",
                percentage
        );

        intent.putExtra(
                "strengths",
                strengths
        );

        intent.putExtra(
                "areasImprovement",
                areasImprovement
        );

        intent.putExtra(
                "timeTakenMillis",
                timeTakenMillis
        );

        intent.putExtra(
                "completionTimeMillis",
                System.currentTimeMillis()
        );

        AssessmentRepository.saveBaselineResult(
                "PASTE_YOUR_ASSESSMENT_ID_HERE",
                correctCount,
                percentage,
                strengths,
                areasImprovement,
                new AssessmentRepository.Callback<Unit>() {
                    @Override
                    public void onSuccess(Unit result) {
                        // No UI action needed — navigation continues below regardless.
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(
                                BaselineTestActivity.this,
                                "Result not saved to server: " + message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        super.onDestroy();
    }

    // ================================================================
    // QUESTION CLASS
    // ================================================================

    private static class Question {

        private final String category;
        private final String question;
        private final String[] answers;
        private final int correctAnswer;

        Question(
                String category,
                String question,
                String[] answers,
                int correctAnswer
        ) {
            this.category = category;
            this.question = question;
            this.answers = answers;
            this.correctAnswer = correctAnswer;
        }

        public String getCategory() {
            return category;
        }

        public String getQuestion() {
            return question;
        }

        public String[] getAnswers() {
            return answers;
        }

        public int getCorrectAnswer() {
            return correctAnswer;
        }
    }
}