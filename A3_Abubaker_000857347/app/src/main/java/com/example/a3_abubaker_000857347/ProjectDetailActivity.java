package com.example.a3_abubaker_000857347;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
/**
 *  I, Ali Abubaker,000857347 certify that this material is my original work. No other person's work has been used without due acknowledgement .
 * Activity that displays detailed information about a science fair project.
 * <p>
 * This activity shows all available project details including:
 * <ul>
 *   <li>Project image (when available)</li>
 *   <li>Year and title</li>
 *   <li>Student names</li>
 *   <li>School information</li>
 *   <li>Full project description</li>
 * </ul>
 * The activity handles state preservation during configuration changes and
 * maintains scroll position for better user experience.
 */
public class ProjectDetailActivity extends AppCompatActivity {
    /** Key for saving scroll position in saved state */
    private static final String SCROLL_POSITION_KEY = "scroll_position";

    /** Key for saving image URL in saved state */
    private static final String IMAGE_URL_KEY = "image_url";

    /** Key for saving project data in saved state */
    private static final String PROJECT_DATA_KEY = "project_data";

    /** Image view for displaying project photo */
    private ImageView imageViewProject;

    /** Text views for displaying project details */
    private TextView textViewYear, textViewTitle, textViewStudents,
            textViewSchool, textViewDescription;

    /** Current scroll position of the content */
    private int scrollPosition = 0;

    /** Container view that holds scrollable content */
    private ViewGroup scrollContainer;

    /** URL of the project image */
    private String imageUrl;

    /** Current project being displayed */
    private Project currentProject;
    /**
     * Initializes the activity and sets up the UI.
     *
     * @param savedInstanceState Contains saved state if recreating after configuration change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        initializeViews();

        if (savedInstanceState != null) {
            // Restore from saved state
            restoreFromSavedState(savedInstanceState);
        } else {
            // Get project from intent
            currentProject = (Project) getIntent().getSerializableExtra(
                    AppConstants.EXTRA_PROJECT);
            if (currentProject != null) {
                updateViews(currentProject);
            }
        }
    }

    /**
     * Initializes all view references by finding them in the layout.
     */
    private void initializeViews() {
        imageViewProject = findViewById(R.id.imageViewProject);
        textViewYear = findViewById(R.id.textViewYear);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewStudents = findViewById(R.id.textViewStudents);
        textViewSchool = findViewById(R.id.textViewSchool);
        textViewDescription = findViewById(R.id.textViewDescription);
        scrollContainer = findViewById(R.id.scrollContainer);
    }

    /**
     * Restores activity state from saved instance.
     *
     * @param savedInstanceState Bundle containing saved state
     */
    private void restoreFromSavedState(Bundle savedInstanceState) {
        scrollPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY, 0);
        imageUrl = savedInstanceState.getString(IMAGE_URL_KEY);
        currentProject = (Project) savedInstanceState.getSerializable(PROJECT_DATA_KEY);

        // Restore text fields
        textViewYear.setText(savedInstanceState.getString("year"));
        textViewTitle.setText(savedInstanceState.getString("title"));
        textViewStudents.setText(savedInstanceState.getString("students"));
        textViewSchool.setText(savedInstanceState.getString("school"));
        textViewDescription.setText(savedInstanceState.getString("description"));

        // Reload image if URL exists
        if (imageUrl != null && !imageUrl.isEmpty()) {
            new ImageDownloadAsyncTask(imageViewProject).execute(imageUrl);
        } else {
            imageViewProject.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    /**
     * Saves current activity state.
     *
     * @param outState Bundle in which to place saved state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save scroll position
        if (scrollContainer instanceof ScrollView) {
            outState.putInt(SCROLL_POSITION_KEY,
                    ((ScrollView)scrollContainer).getScrollY());
        }

        // Save image URL
        outState.putString(IMAGE_URL_KEY, imageUrl);

        // Save project data
        if (currentProject != null) {
            outState.putSerializable(PROJECT_DATA_KEY, currentProject);
        }

        // Save text fields
        outState.putString("year", textViewYear.getText().toString());
        outState.putString("title", textViewTitle.getText().toString());
        outState.putString("students", textViewStudents.getText().toString());
        outState.putString("school", textViewSchool.getText().toString());
        outState.putString("description", textViewDescription.getText().toString());
    }

    /**
     * Restores instance state after recreation.
     *
     * @param savedInstanceState Bundle containing saved state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scrollPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY, 0);
    }

    /**
     * Handles window focus changes to restore scroll position.
     *
     * @param hasFocus Whether the window has focus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && scrollContainer instanceof ScrollView) {
            ((ScrollView)scrollContainer).post(() ->
                    ((ScrollView)scrollContainer).scrollTo(0, scrollPosition));
        }
    }

    /**
     * Updates all views with data from the specified project.
     *
     * @param project The project whose data should be displayed
     */
    private void updateViews(Project project) {
        // Store the project and image URL
        currentProject = project;
        imageUrl = project.picURI;

        // Update text views
        textViewYear.setText(String.format("%s %s",
                getString(R.string.label_year), project.Year));
        textViewTitle.setText(String.format("%s %s",
                getString(R.string.label_title), project.Title));

        String students = String.format("%s %s",
                getString(R.string.label_students), project.NameFull);
        if (project.NameFull2 != null && !project.NameFull2.isEmpty()) {
            students += " & " + project.NameFull2;
        }
        textViewStudents.setText(students);

        String school = String.format("%s %s, %s",
                getString(R.string.label_school),
                project.SchoolName,
                project.SchoolCity);
        textViewSchool.setText(school);

        textViewDescription.setText(project.Description);

        // Load image
        if (project.picURI != null && !project.picURI.isEmpty()) {
            new ImageDownloadAsyncTask(imageViewProject).execute(project.picURI);
        } else {
            imageViewProject.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}