package authorization;

import database.exceptions.AccessControlError;
import database.exceptions.NotOwnerException;
import database.exceptions.PageNotFollowed;
import models.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

public interface AccessController {

    Role newRole(String roleId);

    void setRole(Account user, Role role);

    Roles getRoles(Account user);

    void grantPermission(Role role, Resource res, Operation op);

    void revokePermission(Role role, Resource res, Operation op);

    List<Capability> createKey(Account user);

    void checkPermission(HttpServletRequest request, Resource res, Operation op, Account acc) throws AccessControlError;

    Role getRole(String roleId);

    PageObject checkPage(int page, Account acc) throws  SQLException, NotOwnerException;


    List<PostObject> checkPagePosts(int page, Account acc) throws SQLException, PageNotFollowed;

    void updatePost(int postId, String post_text, Account account) throws NotOwnerException, SQLException;

    void updatePage(int pageId, String pageTitle, String pagePic, String email,String user, Account account) throws NotOwnerException, SQLException;

    void likePost(int postId, Account account) throws SQLException, PageNotFollowed;
    void unlikePost(int postId, Account account) throws SQLException, PageNotFollowed;

    void createPage(String username, String email, String pageTitle, String pagePic) throws SQLException;

    PageObject deletePage(int pageId) throws SQLException;

    PostObject newPost(int pageId, String date, String text, Account account) throws NotOwnerException, SQLException;

    List<PageObject> getPages() throws SQLException;// = app.getAllPages();

    PostObject deletePost(int postId,Account account) throws SQLException, NotOwnerException;
    void updateFollowStatus(int ownerPage, int followerPage, Account account) throws SQLException, NotOwnerException;

    void submitFollowRequest(int ownerPage, int followerPage,Account account) throws NotOwnerException, SQLException;

    int getOwnerPage(Account account) throws SQLException;
}