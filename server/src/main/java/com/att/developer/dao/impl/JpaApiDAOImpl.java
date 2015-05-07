package com.att.developer.dao.impl;

import org.springframework.stereotype.Component;

import com.att.developer.bean.api.Api;
import com.att.developer.dao.ApiDAO;

@Component
public class JpaApiDAOImpl extends JpaDAO<Api> implements ApiDAO {

    public JpaApiDAOImpl() {
        super(Api.class);
    }

}
