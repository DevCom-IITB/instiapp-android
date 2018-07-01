package in.ac.iitb.gymkhana.iitbapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "roles")


public class Role {

    @PrimaryKey(autoGenerate = true)
    int db_id;

    @ColumnInfo(name = "id")
    @SerializedName("id")
    String roleID;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    String roleName;

    @ColumnInfo(name = "inheritable")
    @SerializedName("inheritable")
    boolean roleInheritable;

    @ColumnInfo(name = "body")
    @SerializedName("body")
    String roleBody;

    @ColumnInfo(name = "body_detail")
    @SerializedName("body_detail")
    Body roleBodyDetails;

    @ColumnInfo(name = "bodies")
    @SerializedName("bodies")
    List<Body> roleBodies;


    @ColumnInfo(name = "permissions")
    @SerializedName("permissions")
    List<String> rolePermissions;

    @ColumnInfo(name = "users")
    @SerializedName("users")
    List<String> roleUsers;

    @ColumnInfo(name = "users_detail")
    @SerializedName("users_detail")
    List<User> roleUsersDetail;

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
}
