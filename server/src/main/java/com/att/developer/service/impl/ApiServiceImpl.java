package com.att.developer.service.impl;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.att.developer.bean.api.Api;
import com.att.developer.bean.api.ApiBundle;
import com.att.developer.dao.ApiBundleDAO;
import com.att.developer.dao.ApiDAO;
import com.att.developer.dao.ApiWrapperDAO;
import com.att.developer.service.ApiService;

@Component
public class ApiServiceImpl implements ApiService {

	@Resource
    private ApiDAO apiDAO;
    
    @Resource
    private ApiWrapperDAO apiWrapperDAO;

    @Resource
    private ApiBundleDAO apiBundleDAO;

	public void setApiDAO(ApiDAO apiDAO) {
		this.apiDAO = apiDAO;
	}

	public void setApiWrapperDAO(ApiWrapperDAO apiWrapperDAO) {
		this.apiWrapperDAO = apiWrapperDAO;
	}

	public void setApiBundleDAO(ApiBundleDAO apiBundleDAO) {
		this.apiBundleDAO = apiBundleDAO;
	}

	/* (non-Javadoc)
	 * @see com.att.developer.service.impl.ApiService#createApi(com.att.developer.bean.api.Api)
	 */
	@Override
	@Transactional
    public Api createApi(Api api) {
    	return apiDAO.create(api);
    }
	
	/* (non-Javadoc)
	 * @see com.att.developer.service.impl.ApiService#createApiBundle(com.att.developer.bean.api.ApiBundle)
	 */
	@Override
	@Transactional
	public ApiBundle createApiBundle(ApiBundle apiBundle) {
		return apiBundleDAO.create(apiBundle);
	}
}
