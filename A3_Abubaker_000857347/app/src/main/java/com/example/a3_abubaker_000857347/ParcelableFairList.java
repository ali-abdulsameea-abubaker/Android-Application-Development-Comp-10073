package com.example.a3_abubaker_000857347;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *  I, Ali Abubaker,000857347 certify that this material is my original work. No other person's work has been used without due acknowledgement .
 * Wrapper class that makes a FairList parcelable for Android inter-process communication.
 * <p>
 * This class implements the Parcelable interface to allow FairList objects to be passed
 * between Android components (Activities, Fragments, Services) via Intents. It handles
 * the serialization and deserialization of FairList data.
 */
public class ParcelableFairList implements Parcelable {
    /** The wrapped FairList containing project data */
    private FairList fairList;

    /**
     * Constructs a new ParcelableFairList wrapping the specified FairList.
     *
     * @param fairList the FairList to be made parcelable
     */
    public ParcelableFairList(FairList fairList) {
        this.fairList = fairList;
    }

    /**
     * Constructs a ParcelableFairList from a Parcel (used during deserialization).
     *
     * @param in the Parcel containing the serialized FairList data
     */
    protected ParcelableFairList(Parcel in) {
        fairList = new FairList();
        in.readList(fairList, Project.class.getClassLoader());
    }

    /**
     * Creator object that generates instances of ParcelableFairList from a Parcel.
     */
    public static final Creator<ParcelableFairList> CREATOR = new Creator<ParcelableFairList>() {
        /**
         * Creates a new ParcelableFairList from the given Parcel.
         *
         * @param in the Parcel containing the FairList data
         * @return a new ParcelableFairList instance
         */
        @Override
        public ParcelableFairList createFromParcel(Parcel in) {
            return new ParcelableFairList(in);
        }

        /**
         * Creates a new array of ParcelableFairList objects.
         *
         * @param size the size of the array
         * @return an array of ParcelableFairList objects
         */
        @Override
        public ParcelableFairList[] newArray(int size) {
            return new ParcelableFairList[size];
        }
    };

    /**
     * Describes special contents of this Parcelable's marshalled representation.
     *
     * @return 0 indicating no special contents
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flattens this object into a Parcel.
     *
     * @param dest the Parcel in which the object should be written
     * @param flags additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(fairList);
    }
}