package com.ist.message.service;

import com.ist.message.controller.model.*;

public interface GroupService {
    BaseResp createGroup(CreateGroupReq req);
    BaseResp addGroup(AddGroupReq req);

    BaseApiResp getGroupInfobyIds(GetGroupInfoByIdsReq req);

    BaseApiResp getGroupMemInfobyIds(GetGroupMemInfobyIdsReq req);

    BaseApiResp getGroupMembyId(GetGroupMembyIdReq req);
}
