package com.mandaptak.android.FullProfile;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(manifest="src/main/AndroidManifestTest.xml", sdk = 21)
public class BasicProfileInfoTest {

	private BasicProfileInfo basicProfileInfo;

	@Before
	public void setUp() throws Exception {
		basicProfileInfo = new BasicProfileInfo();
		startFragment(basicProfileInfo);
	}

	@Test
	public void shouldShowUserVisibleHint() throws Exception {
			basicProfileInfo.setUserVisibleHint(true);
			assertThat(basicProfileInfo.isVisible, is(true));
	}

	@Test
	public void shouldNotShowUserVisibleHint() throws Exception {
		basicProfileInfo.isVisible = true;
		basicProfileInfo.setUserVisibleHint(false);
		assertThat(basicProfileInfo.isVisible, is(false));
	}
}