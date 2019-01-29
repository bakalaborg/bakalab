package org.bakalab.app.interfaces;

import org.bakalab.app.items.akce.AkceRoot;
import org.bakalab.app.items.main.MainScreen;
import org.bakalab.app.items.rozvrh.RozvrhRoot;
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
    Call<RozvrhRoot> getRozvrh(@Query("hx") String token, @Query("pmd") String date);

    @GET("login.aspx?pm=all")
    Call<MainScreen> getMain(@Query("hx") String token);

    @GET("login.aspx?pm=akce")
    Call<AkceRoot> getAkce(@Query("hx") String token);

    // TODO Sergeji sem absenci vec

}
