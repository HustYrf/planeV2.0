package hust.plane.web.controller.admin;

import hust.plane.constant.WebConst;
import hust.plane.mapper.pojo.User;
import hust.plane.service.interFace.DepartmentService;
import hust.plane.service.interFace.UserGroupService;
import hust.plane.service.interFace.UserService;
import hust.plane.utils.page.TailPage;
import hust.plane.utils.page.UserPojo;
import hust.plane.utils.pojo.JsonView;
import hust.plane.utils.pojo.TipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;


@Controller
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;
    @Resource
    private UserGroupService userGroupService;
    @Resource
    private DepartmentService departmentService;

//    @RequestMapping(value = "searchUser", method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
//    @ResponseBody
//    public String doSearchUser(@RequestParam String searchUserStatus, @RequestParam(required = false) String searchUserId,
//                               TailPage<User> page) {
//        if (StringUtils.isBlank(searchUserId)) {
//            searchUserId = WebConst.SEARCH_NO_USERID;
//        }
//        page = userService.getUserByRoleOrIdWithPage(searchUserStatus, searchUserId, page);
//        pageList.add(page);
//        return JsonView.render(0,WebConst.SUCCESS_RESULT);
//    }


    @RequestMapping(value = "toUserCreate", method = RequestMethod.GET)
    public String userModify(TailPage<UserPojo> page, Model model, User user) {
//        if("-1".equals(user.getRole()))
//        {
//            user.setRole(null);
//        }
//        if (user.getId()==null || StringUtils.isNotBlank(user.getRole())) {
//            String searchUserStatus = user.getRole();
//            String searchUserId = user.getUserid();
//            if (StringUtils.isBlank(searchUserId)) {
//                searchUserId = WebConst.SEARCH_NO_USERID;
//            }
//            page = userService.getUserByRoleOrIdWithPage(searchUserStatus, searchUserId, page);
//        } else {
        page = userService.getAllUserWithPage(page);
        List<UserPojo> pojoList = page.getItems();
        Iterator<UserPojo> iterator = pojoList.iterator();
        while (iterator.hasNext()) {
            UserPojo userPojo = iterator.next();
            List<Integer> userGroupList = userGroupService.selectGroupIdWithUserId(userPojo.getId());
            if (userGroupList.size() > 0 && userGroupList.contains(Integer.valueOf(1))) {
                userPojo.setPosition("浏览者");
            } else if (userGroupList.size() > 0 && userGroupList.contains(Integer.valueOf(2))) {
                userPojo.setPosition("任务管理员");
            } else {
                userPojo.setPosition("巡检员");
            }
            if (userPojo.getDepartmentId() != null) {
                userPojo.setDepartmentName(departmentService.getUserDepartmentNameWithPartId(userPojo.getDepartmentId()));
            }
        }
        page.setItems(pojoList);
//        model.addAttribute("selectStatus", user.getRole());
        model.addAttribute("page", page);
        model.addAttribute("curNav", "usersEdit");
        return "userModify";
    }

    @RequestMapping(value = "deleteUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String doDelUser(@RequestParam Integer userid) {
        try {
            userService.delUserById(userid);
        } catch (Exception e) {
            String msg = "删除失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return JsonView.render(1, msg);
        }
        return JsonView.render(0, WebConst.SUCCESS_RESULT);
    }

//    @RequestMapping(value = "modifyUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
//    @ResponseBody
//    public String deModifyUser(@RequestParam Integer userid, @RequestParam String role, @RequestParam String descripte) {
//        try {
//            userService.modifyUserRoleAndDes(userid, role, descripte);
//        } catch (Exception e) {
//            String msg = "修改失败";
//            if (e instanceof TipException) {
//                msg = e.getMessage();
//            } else {
//                LOGGER.error(msg, e);
//            }
//            return JsonView.render(1, msg);
//        }
//        return JsonView.render(0, WebConst.SUCCESS_RESULT);
//    }

    @RequestMapping(value = "addUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String doAddUser(@RequestParam Integer addUserId, @RequestParam String addUsername, @RequestParam String addUserPaw,
                            @RequestParam String addUserRole, @RequestParam String addUserDescripte) {
        try {
            userService.addUserWithInfo(addUserId, addUsername, addUserPaw, addUserRole, addUserDescripte);
        } catch (Exception e) {
            String msg = "添加失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return JsonView.render(1, msg);
        }
        return JsonView.render(0, WebConst.SUCCESS_RESULT);
    }


}
