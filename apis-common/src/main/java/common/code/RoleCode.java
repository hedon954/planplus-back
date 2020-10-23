package common.code;

import lombok.Getter;

/**
 * 角色码
 *
 * @author Hedon Wang
 * @create 2020-10-15 22:04
 */
@Getter
public enum RoleCode {


    /**
     * ====================================
     *          需要根据项目需求来定义
     * ====================================
     */
    NORMAL(0, "普通角色"),
    ADMINISTRATOR(1, "超级管理员"),
    PROJECT_MANAGER(2, "管理员"),
    PROJECT_MEMBER(3, "成员"),
    ;

    private final Integer roleId;
    private final String roleName;

    RoleCode(Integer roleId,String roleName){
        this.roleId = roleId;
        this.roleName = roleName;
    }
}
