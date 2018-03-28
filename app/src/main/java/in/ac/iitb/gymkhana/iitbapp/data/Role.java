package in.ac.iitb.gymkhana.iitbapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by varun on 28-Mar-18.
 */

public class Role {
    @PrimaryKey(autoGenerate = true)
    int db_id;
    @ColumnInfo(name = "id")
    @SerializedName("id")
    String roleID;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    String roleName;
    @ColumnInfo(name = "body_id")
    @SerializedName("body")
    String bodyId;
    @ColumnInfo(name = "body")
    @SerializedName("body_detail")
    Body body;
    @ColumnInfo(name = "permissions")
    @SerializedName("permissions")
    List<String> permissions;
    @ColumnInfo(name = "events")
    @SerializedName("events")
    List<Event> events;
    @ColumnInfo(name = "user_ids")
    @SerializedName("users")
    List<String> userIds;
    @ColumnInfo(name = "user_ids")
    @SerializedName("users_detail")
    List<User> users;

    public Role(String roleID, String roleName, String bodyId, Body body, List<String> permissions, List<Event> events, List<String> userIds, List<User> users) {
        this.roleID = roleID;
        this.roleName = roleName;
        this.bodyId = bodyId;
        this.body = body;
        this.permissions = permissions;
        this.events = events;
        this.userIds = userIds;
        this.users = users;
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

    public String getBodyId() {
        return bodyId;
    }

    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
