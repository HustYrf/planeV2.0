package hust.plane.service.interFace;

import hust.plane.mapper.pojo.User;
import hust.plane.utils.page.TailPage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    User login(String username, String password);

    int register(String username, String password);

    User queryUserById(int id);

    boolean modifyPwd(HttpServletRequest request, String oldpassword, String password);

//	List<User> findByUserRole(User userExmple);

    TailPage<User> getAllUserWithPage(TailPage<User> page);

    int delUserById(Integer userid);

//    int modifyUserRoleAndDes(Integer userid, String role, String descripte);

    int addUserWithInfo(Integer addUserId, String addUsername, String addUserPaw, String addUserRole, String addUserDescripte);

//    TailPage<User> getUserByRoleOrIdWithPage(String searchUserStatus, String searchUserId, TailPage<User> page);

    boolean updataTasknumByUser(User user);

    boolean reduceTasknumByUser(User user);

	User getUserById(Integer userbid);

    List<User> fuzzySearchWithUser(String queryString);

	int modifyUserRoleAndDes(int userid, String descripte);
}
