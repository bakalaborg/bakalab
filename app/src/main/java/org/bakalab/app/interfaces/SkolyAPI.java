package org.bakalab.app.interfaces;

import org.bakalab.app.items.skoly.Mesto;
import org.bakalab.app.items.skoly.SkolyRoot;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SkolyAPI {
    @GET("municipality")
    Call<SkolyRoot> getMesta();

    @GET("municipality/{city}")
    Call<Mesto> getMesto(@Path("city") String city);

}
