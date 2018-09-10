package hust.plane.mapper.mapper;

import java.util.List;

import hust.plane.mapper.pojo.User_has_GroupKey;


public interface User_has_GroupKeyMapper {

    List<User_has_GroupKey> getAllGroupByUserId(int UserId);

    List<Integer> getGroupIdByUserId(int id);
}
