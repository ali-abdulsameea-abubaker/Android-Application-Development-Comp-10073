package com.example.a3_abubaker_000857347;
import java.io.Serializable;
/**
 *  I, Ali Abubaker,000857347 certify that this material is my original work. No other person's work has been used without due acknowledgement .
 * Represents a Science Fair Project with all relevant fields from the Web API.
 * This class is used by GSON to deserialize JSON responses and passed between
 * activities to display project details.
 */
public class Project implements Serializable {


    /** Unique ID for the project */
    public String _id;
    /** Year the project was submitted */
    public String Year;
    /** Project number identifier */
    public String ProjectNum;
    /** Full name of first student */
    public String NameFull;
    /** Full name of second student (may be empty) */
    public String NameFull2;
    /** Combined names of both students */
    public String NameBoth;
    /** Project level (Junior/Senior) */
    public String Level;
    /** School name */
    public String SchoolName;
    /** School board */
    public String SchoolBoard;
    /** School city */
    public String SchoolCity;
    /** Project title */
    public String Title;
    /** Project description */
    public String Description;
    /** URL to project image (may be empty) */
    public String picURI;

    /**
     * Returns a string representation of the project (for debugging)
     * @return String containing project details
     */
    @Override
    public String toString() {
        return Year + " " + Title;
    }
}