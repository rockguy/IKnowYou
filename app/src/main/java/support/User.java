package support;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 11.03.2017.
 */

public class User {
    @SerializedName("uid")
    public int Id;
    @SerializedName("first_name")
    public String FirstName;
    @SerializedName("last_name")
    public String LastName;
}
