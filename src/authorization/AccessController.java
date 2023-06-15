package authorization;

import database.exceptions.AccessControlError;
import database.exceptions.AuthenticationError;
import database.exceptions.NotOwnerException;
import database.exceptions.PageNotFollowed;
import models.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

public interface AccessController {

    Role newRole(String roleId);

    void setRole(Account user, Role role);

    List<Role> getRoles(Account user);

    void grantPermission(Role role, Resource res, Operation op);

    void revokePermission(Role role, Resource res, Operation op);

    List<Capability> makeKey(List<Role> roles);

    void checkPermission(List<Capability> capabilities, Resource res, Operation op, Account acc) throws AccessControlError;

    Role getRole(String roleId);

    PageObject checkPage(int page, Account acc) throws  SQLException, NotOwnerException;

    PostObject checkPost(int post, Account acc) throws SQLException, NotOwnerException;

    List<PostObject> checkPagePosts(int page, Account acc) throws SQLException, PageNotFollowed;

    void updatePost(int postId, String post_text, Account account) throws NotOwnerException, SQLException;

    void updatePage(int pageId, String pageTitle, String pagePic, String email,String user, Account account) throws NotOwnerException, SQLException;

    void likePost(int postId, Account account) throws SQLException, PageNotFollowed;
    void unlikePost(int postId, Account account) throws SQLException, PageNotFollowed;
    List<Capability> getCapabilities(HttpServletRequest request, String username) throws AuthenticationError;

}