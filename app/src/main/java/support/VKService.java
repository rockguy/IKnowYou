package support;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by User on 11.03.2017.
 */

public interface VKService {

    @GET("method/users.get")
    Call<VKResponse<List<User>>> getInformation(@Query("access_token") String accessToken);

    @GET("method/users.get")
    Call<VKResponse> addFriend(@Query("user_id") String user, @Query("v") String version, @Query("access_token") String accessToken);
}
