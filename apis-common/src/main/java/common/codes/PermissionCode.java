package common.codes;

import lombok.Getter;

/**
 * 权限码
 *
 * @author Hedon Wang
 * @create 2020-10-15 22:08
 */
@Getter
public enum PermissionCode {

    /**
     * ====================================
     *          需要根据项目需求来定义
     * ====================================
     */
    READ(101,"读"),
    WRITE(102,"写")

    ;




    private final Integer permissionId;
    private final String permissionName;

    PermissionCode(Integer permissionId, String permissionName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
    }
}
