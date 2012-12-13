package eu.vranckaert.worktime.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * User: Dirk Vranckaert
 * Date: 13/12/12
 * Time: 11:30
 */
@DatabaseTable(tableName = "User")
public class User {
    @DatabaseField(id = true, generatedId = false, columnName = "email", dataType = DataType.STRING)
    private String email;
    @DatabaseField
    private String password;
    @DatabaseField
    private String sessionKey;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
}
