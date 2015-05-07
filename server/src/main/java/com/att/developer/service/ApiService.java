package com.att.developer.service;

import java.util.List;

import com.att.developer.bean.api.Api;
import com.att.developer.bean.api.ApiBundle;
import com.att.developer.bean.api.ApiWrapper;

public interface ApiService {

	Api createApi(Api api);

	ApiBundle createApiBundle(ApiBundle apiBundle);

	List<ApiWrapper> getApis();

}