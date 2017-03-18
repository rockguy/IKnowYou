package support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 11.03.2017.
 */

public class VKResponse {
    @SerializedName("response")
    @Expose
    private Integer response = null;

    public Integer getResponseCode() {
        return response;
    }
}
