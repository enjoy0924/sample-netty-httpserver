package com.altas.core.annotation.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionConstraint {

    public List<String> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(String permission){
        this.permissions.add(permission);
    }

    private List<String> permissions = new ArrayList<>();

    public void addPermissions(String permissions, String seprator) {
        this.permissions.addAll(
                Arrays.asList(permissions.split(seprator))
        );
    }
}
