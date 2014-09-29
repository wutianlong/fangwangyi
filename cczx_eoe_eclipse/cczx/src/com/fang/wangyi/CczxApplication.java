package com.fang.wangyi;

import c.i.a.u;

import com.baidu.frontia.FrontiaApplication;

/**
 * Created by changliang on 14-7-3. email: clemu@qq.com
 */
public class CczxApplication extends FrontiaApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		u.i(getApplicationContext(), 0);
	}
}
