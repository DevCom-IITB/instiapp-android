package app.insti.api.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.insti.interfaces.CardInterface;

public class Role implements CardInterface {
    @NonNull()
    @SerializedName("id")
    private String roleID;

    @SerializedName("name")
    private String roleName;

    @SerializedName("inheritable")
    private boolean roleInheritable;

    @SerializedName("body")
    private String roleBody;

    @SerializedName("body_detail")
    private Body roleBodyDetails;

    @SerializedName("bodies")
    private List<Body> roleBodies;

    @SerializedName("permissions")
    private List<String> rolePermissions;

    @SerializedName("users")
    private List<String> roleUsers;

    @SerializedName("users_detail")
    private List<User> roleUsersDetail;

    public Role(String roleID, String roleName, boolean roleInheritable, String roleBody, Body roleBodyDetails, List<Body> roleBodies, List<String> rolePermissions, List<String> roleUsers, List<User> roleUsersDetail) {
        this.roleID = roleID;
        this.roleName = roleName;
        this.roleInheritable = roleInheritable;
        this.roleBody = roleBody;
        this.roleBodyDetails = roleBodyDetails;
        this.roleBodies = roleBodies;
        this.rolePermissions = rolePermissions;
        this.roleUsers = roleUsers;
        this.roleUsersDetail = roleUsersDetail;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean isRoleInheritable() {
        return roleInheritable;
    }

    public void setRoleInheritable(boolean roleInheritable) {
        this.roleInheritable = roleInheritable;
    }

    public String getRoleBody() {
        return roleBody;
    }

    public void setRoleBody(String roleBody) {
        this.roleBody = roleBody;
    }

    public Body getRoleBodyDetails() {
        return roleBodyDetails;
    }

    public void setRoleBodyDetails(Body roleBodyDetails) {
        this.roleBodyDetails = roleBodyDetails;
    }

    public List<Body> getRoleBodies() {
        return roleBodies;
    }

    public void setRoleBodies(List<Body> roleBodies) {
        this.roleBodies = roleBodies;
    }

    public List<String> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(List<String> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public List<String> getRoleUsers() {
        return roleUsers;
    }

    public void setRoleUsers(List<String> roleUsers) {
        this.roleUsers = roleUsers;
    }

    public List<User> getRoleUsersDetail() {
        return roleUsersDetail;
    }

    public void setRoleUsersDetail(List<User> roleUsersDetail) {
        this.roleUsersDetail = roleUsersDetail;
    }

    public long getId() {
        return getRoleID().hashCode();
    }

    public String getTitle() {
        return getRoleBodyDetails().getBodyName();
    }

    public String getSubtitle() {
        return getRoleName();
    }

    public String getAvatarUrl() {
        return getRoleBodyDetails().getBodyImageURL();
    }
}
