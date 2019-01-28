package org.bakalab.app.interfaces;

import org.bakalab.app.items.main.MainScreen;
import org.bakalab.app.items.rozvrh.Rozvrh;
import org.bakalab.app.items.ukoly.UkolyList;
import org.bakalab.app.items.znamky.ZnamkaPredmetyList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BakalariAPI {
    @GET("znamky.xml")
    Call<ZnamkaPredmetyList> getZnamky(@Query("hx") String token);

    @GET("login.aspx?pm=ukoly")
    Call<UkolyList> getUkoly(@Query("hx") String token);

    @GET("login.aspx?pm=rozvrh")
    Call<Rozvrh> getRozvrh(@Query("hx") String token);

    @GET("login.aspx?pm=all")
    Call<MainScreen> getMain(@Query("hx") String token);

    // TODO Sergeji sem absenci vec

}
