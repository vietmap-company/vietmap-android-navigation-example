package vn.vietmap.viet_navigation.interfaces;
import androidx.annotation.Nullable;
import vn.vietmap.viet_navigation.models.VietMapV3PlaceModel;
import vn.vietmap.viet_navigation.models.VietMapV3SearchResponse;
import com.mapbox.api.directions.v5.models.DirectionsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface RetrofitAPI {

    @GET("api/autocomplete/v3")
    Call<List<VietMapV3SearchResponse>> autocomplete(@Query("text") String keySearch, @Query("apikey") String apikey, @Nullable @Query("focus")String latLongFocus);

    @GET("api/place/v3")
    Call<VietMapV3PlaceModel> getPlaceData(@Query("refId") String refId, @Query("apikey") String apikey);

    @GET("https://maps.vietmap.vn/api/navigations/route/directions/v5/mapbox/driving-traffic/{latLong}")
    Call<DirectionsResponse> routing(@Path ("latLong") String latLong, @Query("apikey") String apikey,@Query("bearing") String bearing);

}
