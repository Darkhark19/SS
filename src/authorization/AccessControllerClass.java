package authorization;

import authenticator.utils.JWTUtils;
import database.DatabaseOperator;
import database.SN;
import database.exceptions.AccessControlError;
import database.exceptions.NotOwnerException;
import database.exceptions.PageNotFollowed;
import database.exceptions.TimeExpiredTokenError;
import models.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.*;

public class AccessControllerClass implements AccessController {

    private static final String CAPABILITY = "capability";
    private final DatabaseOperator db;

    private static final SN app = SN.getInstance();

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
        PageObject p = app.getPage(page);
        if (!p.getUserId().equals(acc.getUsername()))
            throw new NotOwnerException();
        return p;
    }

    public PostObject checkPost(int post, Account acc) throws NotOwnerException, SQLException {
        PostObject p = app.getPost(post);
        checkPage(p.getPageId(), acc);
        return p;
    }

    @Override
    public List<PostObject> checkPagePosts(int page, Account acc) throws SQLException, PageNotFollowed {
        List<PageObject> pages = app.getFollowers(page);
        for (PageObject p : pages) {
            if (p.getUserId().equals(acc.getUsername()))
                return app.getPagePosts(page);
        }
        throw new PageNotFollowed();

    }

    @Override
    public void updatePost(int postId, String post_text, Account account) throws NotOwnerException, SQLException {
        PostObject p = checkPost(postId, account);
        p.setPostText(post_text);
        app.updatePost(p);

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
        app.updatePage(p);
    }

    @Override
    public void likePost(int postId, Account account) throws SQLException, PageNotFollowed {
        PageObject ownerPage = app.getOwnerPage(account);
        PostObject post = app.getPost(postId);
        List<PageObject> followers = app.getFollowed(ownerPage.getPageId());
        for (PageObject p : followers) {
            if (p.getPageId() == post.getPageId()){
                app.like(postId, post.getPageId());
                return;
            }
        }
        throw new PageNotFollowed();
    }

    @Override
    public void unlikePost(int postId, Account account) throws SQLException, PageNotFollowed {
        PostObject post = app.getPost(postId);
        List<PageObject> followers = app.getFollowers(post.getPageId());
        for (PageObject p : followers) {
            if (p.getUserId().equals(account.getUsername())) {
                app.unlike(postId, post.getPageId());
                return;
            }
        }
        throw new PageNotFollowed();

    }

    @Override
    public void createPage(String username, String email, String pageTitle, String pagePic) throws SQLException {
        app.newPage(username,email,pageTitle,pagePic);

    }

    @Override
    public PageObject deletePage(int pageId) throws SQLException {
        PageObject page = app.getPage(pageId);
        app.deletePage( page);
        return page;
    }

    @Override
    public PostObject newPost(int pageId, String date, String text, Account account) throws NotOwnerException, SQLException {
        this.checkPage(pageId, account);
        return app.newPost(pageId,date,text);
    }

    @Override
    public List<PageObject> getPages() throws SQLException {
         return app.getAllPages();
    }

    @Override
    public PostObject deletePost(int postId,Account account) throws NotOwnerException, SQLException {
        PostObject p = this.checkPost(postId, account);
        app.deletePost( p);
        return p;
    }

    @Override
    public void updateFollowStatus(int ownerPage, int followerPage, Account account) throws SQLException, NotOwnerException {
        checkPage(ownerPage, account);
        app.updateFollowsStatus(ownerPage, followerPage, FState.OK);
    }

    @Override
    public void submitFollowRequest(int ownerPage, int followerPage, Account account) throws NotOwnerException, SQLException {
        this.checkPage(ownerPage, account);
        app.follows(ownerPage, followerPage, FState.PENDING);
    }

    @Override
    public int getOwnerPage(Account account) throws SQLException {
        return app.getOwnerPage(account).getPageId();
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
                Date expire = new Date(System.currentTimeMillis() + (10 * 60 * 1000));
                e.getValue().forEach(value -> result.add(new Capability(user.getUsername()).makeKey(e.getKey(), value, expire)));
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
        boolean hasPermission = false;
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.contains(CAPABILITY)) {
                //se token null ou se check permission retornar false
                //ir a base de dados buscar permissoes pedidas do user
                try {
                    if(this.checkCapabilityToken(session, res, op, key, acc.getUsername())) {
                        hasPermission = true;
                        break;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(!hasPermission)
            throw new AccessControlError();
    }

    /**
     * Verifica se o token é válido e se o utilizador tem permissão para aceder ao recurso
     * @param session
     * @param res
     * @param op
     * @param key
     * @param username
     * @throws SQLException
     * @throws AccessControlError
     */
    private boolean checkCapabilityToken(HttpSession session, Resource res, Operation op, String key, String username) throws SQLException, AccessControlError {
        String token = session.getAttribute(key).toString();
        Capability cap = JWTUtils.parseCapabilityJWT(token, session.getId(), username);
        boolean isValid = true;
        try {
            if (cap == null) {
                isValid = false;
            } else if (cap.checkPermission(res, op, username))
                return true;
        } catch (TimeExpiredTokenError e) {
            isValid = false;
        }
        if (!isValid) {
            boolean result = db.checkPermission(username, res, op);
            if (result) {
                UUID uuid = UUID.randomUUID();
                cap = new Capability(username);
                Date expire = new Date(System.currentTimeMillis() + (10 * 60 * 1000));
                session.setAttribute(CAPABILITY+uuid, cap.makeKey(res, op, expire));
            } else {
                session.removeAttribute(key);
                throw new AccessControlError();
            }
        }
        return false;
    }

}

