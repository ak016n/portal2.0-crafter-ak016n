package com.att.developer.service;

import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;


public interface UserCreator {

    SessionUser buildSessionUserFromUserEntity(User portalUser);
}
