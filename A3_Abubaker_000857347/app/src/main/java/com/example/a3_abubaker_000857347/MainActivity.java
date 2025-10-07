package com.example.a3_abubaker_000857347;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.StringJoiner;

/**
 *  I, Ali Abubaker,000857347 certify that this material is my original work. No other person's work has been used without due acknowledgement .
 * Main activity for the Science Fair Projects application.
 * <p>
 * This activity provides the main interface for searching science fair projects
 * through a web API and displaying the results. It handles:
 * <ul>
 *   <li>User input for search criteria (year and description keywords)</li>
 *   <li>Network connectivity verification</li>
 *   <li>API request construction and execution</li>
 *   <li>JSON response parsing and display</li>
 *   <li>Navigation to project details</li>
 *   <li>State preservation during configuration changes</li>
 * </ul>
 */
public class MainActivity extends AppCompatActivity {
    /** Tag for logging purposes */
    public static final String TAG = AppConstants.TAG_MAIN;

    /** Input field for searching by year */
    private EditText editTextYear;

    /** Input field for searching by description keywords */
    private EditText editTextDescription;

    /** List view for displaying search results */
    private ListView listViewProjects;

    /** Adapter for managing project data in the list view */
    private ArrayAdapter<Project> adapter;

    /** List of projects returned from the most recent search */
    private FairList fairList;

    /**
     * Initializes the activity and sets up UI components.
     *
     * @param savedInstanceState If non-null, contains saved state from a previous configuration change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupEventListeners();
        initializeAdapter();

        Log.d(TAG, "onCreate");
    }

    /**
     * Initializes view references by finding them in the layout.
     */
    private void initializeViews() {
        editTextYear = findViewById(R.id.editTextYear);
        editTextDescription = findViewById(R.id.editTextDescription);
        listViewProjects = findViewById(R.id.listViewProjects);
    }

    /**
     * Sets up click listeners for interactive elements.
     */
    private void setupEventListeners() {
        findViewById(R.id.buttonSearch).setOnClickListener(this::onSearchClick);
        findViewById(R.id.buttonHelp).setOnClickListener(this::onHelpClick);
        listViewProjects.setOnItemClickListener(this::onItemClick);
    }

    /**
     * Initializes the list adapter with an empty data set.
     */
    private void initializeAdapter() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewProjects.setAdapter(adapter);
    }

    /**
     * Handles click events on the search button.
     * <p>
     * Validates network connectivity and constructs an API request URL based on
     * user input before initiating an asynchronous download task.
     *
     * @param view The view that triggered the event
     */
    private void onSearchClick(View view) {
        if (!isNetworkAvailable()) {
            showToast("No internet connection");
            return;
        }

        try {
            String url = buildApiUrl(
                    editTextYear.getText().toString().trim(),
                    editTextDescription.getText().toString().trim()
            );

            Log.d(TAG, "Search URL: " + url);
            new DownloadAsyncTask(this).execute(url);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "URL encoding error", e);
            showToast("Error creating search URL");
        }
    }

    /**
     * Constructs the API URL with appropriate query parameters.
     *
     * @param year The year to search for (may be empty)
     * @param description The description keyword to search for (may be empty)
     * @return The complete API URL with encoded parameters
     * @throws UnsupportedEncodingException If UTF-8 encoding is not supported
     */
    private String buildApiUrl(String year, String description)
            throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder(
                "http://www.basef.ca/api/basefws.php");

        if (!year.isEmpty() || !description.isEmpty()) {
            urlBuilder.append("?filter=");
            StringJoiner filterJoiner = new StringJoiner(",");

            if (!year.isEmpty()) {
                filterJoiner.add(URLEncoder.encode(
                        "{\"type\":\"number\",\"column\":\"Year\",\"value\":" + year + "}",
                        "UTF-8"));
            }

            if (!description.isEmpty()) {
                filterJoiner.add(URLEncoder.encode(
                        "{\"type\":\"string\",\"column\":\"Description\",\"value\":\"" +
                                description + "\"}",
                        "UTF-8"));
            }

            urlBuilder.append(URLEncoder.encode("[", "UTF-8"))
                    .append(filterJoiner.toString())
                    .append(URLEncoder.encode("]", "UTF-8"));
        }

        return urlBuilder.toString();
    }

    /**
     * Displays a short toast message to the user.
     *
     * @param message The text to display in the toast
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles click events on the help button.
     * <p>
     * Displays a dialog with instructions for using the application.
     *
     * @param view The view that triggered the event
     */
    private void onHelpClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("How to Use")
                .setMessage(
                        "Search Projects by:\n" +
                                "- Year (e.g., 2021)\n" +
                                "- Description keyword (e.g., 'covid')\n\n" +
                                "Tap any result to view details\n" +
                                "Note: Some projects may not have images"
                )
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * Checks if the device has an active network connection.
     *
     * @return true if network connectivity is available, false otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Processes JSON data received from the API and updates the UI.
     *
     * @param result The JSON string response from the API
     */
    public void processDownloadResult(String result) {
        if (result == null || result.isEmpty()) {
            showToast("Network error or no data received");
            return;
        }

        runOnUiThread(() -> {
            try {
                Gson gson = new Gson();
                fairList = gson.fromJson(result, FairList.class);

                if (fairList == null || fairList.isEmpty()) {
                    showToast("No projects found");
                    adapter.clear();
                } else {
                    adapter.clear();
                    adapter.addAll(fairList);
                    showToast("Found " + fairList.size() + " projects");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                showToast("Error processing data");
                Log.d(TAG, "API Response: " + result);
            }
        });
    }

    /**
     * Handles click events on list view items.
     * <p>
     * Launches the detail activity for the selected project.
     *
     * @param parent The AdapterView where the click happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that was clicked
     */
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (fairList != null && position < fairList.size()) {
            Intent intent = new Intent(this, ProjectDetailActivity.class);
            intent.putExtra(AppConstants.EXTRA_PROJECT, fairList.get(position));
            intent.putExtra(AppConstants.EXTRA_FAIR_LIST, new ParcelableFairList(fairList));
            intent.putExtra(AppConstants.EXTRA_POSITION, position);
            startActivity(intent);
        }
    }

    /**
     * Saves the current state of the activity.
     *
     * @param outState Bundle in which to place saved state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("year", editTextYear.getText().toString());
        outState.putString("description", editTextDescription.getText().toString());

        if (fairList != null) {
            outState.putSerializable("fairList", fairList);
        }
    }

    /**
     * Restores the saved state of the activity.
     *
     * @param savedInstanceState Bundle containing saved state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        editTextYear.setText(savedInstanceState.getString("year"));
        editTextDescription.setText(savedInstanceState.getString("description"));

        if (savedInstanceState.containsKey("fairList")) {
            fairList = (FairList) savedInstanceState.getSerializable("fairList");
            if (fairList != null && adapter != null) {
                adapter.clear();
                adapter.addAll(fairList);
            }
        }
    }
}