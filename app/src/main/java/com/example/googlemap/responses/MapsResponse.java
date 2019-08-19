
package com.example.googlemap.responses;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MapsResponse {

    @SerializedName("rows")
    private List<Row> mRows;

    public List<Row> getRows() {
        return mRows;
    }

    public void setRows(List<Row> rows) {
        mRows = rows;
    }

    public class Row {

        @SerializedName("destination_Long_Lat")
        private List<String> mDestinationLongLat;
        @SerializedName("title")
        private String mTitle;

        public List<String> getDestinationLongLat() {
            return mDestinationLongLat;
        }

        public void setDestinationLongLat(List<String> destinationLongLat) {
            mDestinationLongLat = destinationLongLat;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

    }

}
