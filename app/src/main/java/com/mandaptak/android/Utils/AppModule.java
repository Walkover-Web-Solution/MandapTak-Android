package com.mandaptak.android.Utils;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

	private Common common;

	public AppModule(Common common) {
		this.common = common;
	}

	@Provides
	public Common application() {
		return this.common;
	}

	@Provides
	public Context context(){
		return this.application();
	}


}
