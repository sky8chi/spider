package com.skyzd.framework.http;

import java.io.IOException;

/**
 * parameters for value decorate
 * Created by sky.chi on 4/12/2017 11:14 PM.
 * Email: sky8chi@gmail.com
 */
public interface ParamValueDecorate {
	
	String doInValue(String value) throws IOException;
	
}
