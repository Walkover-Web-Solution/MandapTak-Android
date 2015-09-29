package com.mandaptak.android.Utils;

import android.content.Context;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
	void inject(Common application);

	Common application();
	Context context();
}
