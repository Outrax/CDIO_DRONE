package de.yadrone.apps.paperchase;

import com.google.zxing.Result;

public interface TagListener
{

	public void onTag(Result result, float orientation);
	public void onTags(Result[] result, float orientation);
	
}
