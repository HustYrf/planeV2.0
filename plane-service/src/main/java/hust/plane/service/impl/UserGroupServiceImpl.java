package hust.plane.service.impl;

import hust.plane.mapper.mapper.User_has_GroupKeyMapper;
import hust.plane.service.interFace.UserGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UserGroupServiceImpl implements UserGroupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserGroupServiceImpl.class);
    @Resource
    private User_has_GroupKeyMapper userHasGroupKeyMapper;

    @Override
    public List<Integer> selectGroupIdWithUserId(int id) {
        if (Integer.valueOf(id) == null) {
            LOGGER.error("传入的用户ID为空");
        }
        List<Integer> GroupIdList = null;
        try {
            GroupIdList = userHasGroupKeyMapper.getGroupIdByUserId(id);
            if(GroupIdList==null||GroupIdList.size()==0){
                LOGGER.error("无法获取该用户用户组ID");
            }
        } catch (Exception e) {
            LOGGER.error("获取用户的用户组ID失败", e);
        }
        return GroupIdList;
    }
}
