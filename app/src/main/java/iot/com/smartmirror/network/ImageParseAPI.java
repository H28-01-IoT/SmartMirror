package iot.com.smartmirror.network;

import io.reactivex.Observable;
import iot.com.smartmirror.network.json.cloudvision.CloudVisionResponce;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by watanabe on 2017/09/11.
 */

public interface ImageParseAPI {

    @GET("/{restFileName}")
    Observable<CloudVisionResponce> getParsedInformation(
            @Path(value = "restFileName", encode = false) String fileName,
            @Query("id") String id,
            @Query("appId") String appID);

}
