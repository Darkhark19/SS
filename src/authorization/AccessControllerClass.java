package authorization;

import authenticator.utils.JWTUtils;
import database.DatabaseOperator;
import database.SN;
import database.exceptions.AccessControlError;
import database.exceptions.NotOwnerException;
import database.exceptions.PageNotFollowed;
import models.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.*;

public class AccessControllerClass implements AccessController {

    private static final String CAPABILITY = "capability";
    private final DatabaseOperator db;

    public AccessControllerClass() {
        this.db = new DatabaseOperator();
    }

    @Override
    public Role newRole(String roleId) {
        try {
            db.createRole(roleId);
        } catch (SQLException e) {
            System.out.println("Error creating role");
            throw new RuntimeException(e);

        }
        return Role.valueOf(roleId.toLowerCase());
    }

    @Override
    public void setRole(Account user, Role role) {
        try {
            db.createUserRole(user.getUsername(), role);
        } catch (SQLException e) {
            System.out.println("Error setting role");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Roles getRoles(Account user) {
        try {
            return db.getUserRoles(user.getUsername());
        } catch (SQLException e) {
            System.out.println("Error getting roles");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Role getRole(String roleId) {
        try {
            return db.getRole(roleId);
        } catch (SQLException e) {
            System.out.println("Error getting role");
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageObject checkPage(int page, Account acc) throws NotOwnerException, SQLException {
        PageObject p = SN.getInstance().getPage(page);
        if (!p.getUserId().equals(acc.getUsername()))
            throw new NotOwnerException();
        return p;
    }

    public PostObject checkPost(int post, Account acc) throws NotOwnerException, SQLException {
        PostObject p = SN.getInstance().getPost(post);
        checkPage(p.getPageId(), acc);
        return p;
    }

    @Override
    public List<PostObject> checkPagePosts(int page, Account acc) throws SQLException, PageNotFollowed {
        List<PageObject> pages = SN.getInstance().getfollowers(page);
        for (PageObject p : pages) {
            if (p.getUserId().equals(acc.getUsername()))
                return SN.getInstance().getPagePosts(page);
        }
        throw new PageNotFollowed();

    }

    @Override
    public void updatePost(int postId, String post_text, Account account) throws NotOwnerException, SQLException {
        PostObject p = checkPost(postId, account);
        p.setPostText(post_text);
        SN.getInstance().updatePost(p);

    }

    @Override
    public void updatePage(int pageId, String pageTitle, String pagePic, String email, String user, Account account) throws NotOwnerException, SQLException {
        PageObject p = checkPage(pageId, account);
        if (pageTitle != null)
            p.setPageTitle(pageTitle);
        if (pagePic != null)
            p.setPagePic(pagePic);
        if (email != null)
            p.setEmail(email);
        if (user != null)
            p.setUserId(user);
        SN.getInstance().updatePage(p);
    }

    @Override
    public void likePost(int postId, Account account) throws SQLException, PageNotFollowed {
        SN app = SN.getInstance();
        PostObject post = app.getPost(postId);
        List<PageObject> followers = app.getfollowers(post.getPageId());
        for (PageObject p : followers) {
            if (p.getUserId().equals(account.getUsername())) {
                app.like(postId, post.getPageId());
                return;
            }
        }
        throw new PageNotFollowed();
    }

    @Override
    public void unlikePost(int postId, Account account) throws SQLException, PageNotFollowed {
        SN app = SN.getInstance();
        PostObject post = app.getPost(postId);
        List<PageObject> followers = app.getfollowers(post.getPageId());
        for (PageObject p : followers) {
            if (p.getUserId().equals(account.getUsername())) {
                app.unlike(postId, post.getPageId());
                return;
            }
        }
        throw new PageNotFollowed();

    }


    @Override
    public void grantPermission(Role role, Resource res, Operation op) {
        try {
            db.createPermission(role, res, op);
        } catch (SQLException e) {
            System.out.println("Error granting permission");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void revokePermission(Role role, Resource res, Operation op) {
        try {
            db.deletePermission(role, res, op);
        } catch (SQLException e) {
            System.out.println("Error revoking permission");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Capability> createKey(Account user) {
        Roles userRoles = this.getRoles(user);
        try {
            Map<Resource, Set<Operation>> resultMap = new HashMap<>();
            for (Role role : userRoles.getRoles()) {
                Map<Resource, List<Operation>> permissions = db.getPermissions(role);
                for (Map.Entry<Resource, List<Operation>> e : permissions.entrySet()) {
                    Set<Operation> ops = resultMap.get(e.getKey());
                    if (ops == null)
                        ops = new HashSet<>();
                    ops.addAll(e.getValue());
                    resultMap.put(e.getKey(), ops);
                }
            }
            List<Capability> result = new ArrayList<>();

            for (Map.Entry<Resource, Set<Operation>> e : resultMap.entrySet()) {
                Capability cap = new Capability(user.getUsername());
                Date expire = new Date(System.currentTimeMillis() + (10 * 60 * 1000));
                e.getValue().forEach(value -> result.add(cap.makeKey(e.getKey(), value, expire)));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void checkPermission(HttpServletRequest request, Resource res, Operation op, Account acc) throws AccessControlError {
        HttpSession session = request.getSession();
        if (acc == null)
            throw new AccessControlError();
        Enumeration<String> keys = session.getAttributeNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.contains(CAPABILITY)) {
                String token = session.getAttribute(key).toString();
                Capability cap = JWTUtils.parseCapabilityJWT(token, session.getId(), acc.getUsername());
                if (cap == null) {
                    session.removeAttribute(key);
                } else if (cap.checkPermission(res, op, acc.getUsername()))
                    return;
            }
        }
        throw new AccessControlError();
    }

}

