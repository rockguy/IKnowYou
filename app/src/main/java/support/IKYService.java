package support;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vinnik.iknowyou.MainActivity;

/**
 * Created by User on 18.03.2017.
 */

public interface IKYService {
    @GET("new_id")
    Call<Long> getId();
}
