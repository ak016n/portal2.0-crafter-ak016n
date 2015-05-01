package com.att.developer.service;

import javax.transaction.Transactional;

import com.att.developer.bean.api.Api;
import com.att.developer.bean.api.ApiBundle;

public interface ApiService {

	public abstract Api createApi(Api api);

	public abstract ApiBundle createApiBundle(ApiBundle apiBundle);

}