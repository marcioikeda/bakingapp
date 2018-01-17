package br.com.marcioikeda.bakingapp.app;

import android.app.Application;

import com.facebook.stetho.Stetho;

import br.com.marcioikeda.bakingapp.BuildConfig;

/**
 * Created by marcio.ikeda on 16/01/2018.
 */

public class BakingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build()
            );
        }
    }
}
