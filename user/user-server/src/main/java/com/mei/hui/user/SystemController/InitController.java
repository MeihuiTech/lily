package com.mei.hui.user.SystemController;

import com.mei.hui.user.entity.SysRoleMenu;
import com.mei.hui.user.mapper.SysRoleMenuMapper;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/init")
@Api(tags = "初始化数据")
public class InitController {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    public Result initRoleAndMenu(){

        String meanIds = "1,2016,2007,2008,2009,100,1001,1002,1003,1004,1005,1006,1007,101,1008,1009,1010,1011,1012,102,1013,1014,1015,1016,108,500,1040,1041,1042,501,1043,1044,1045";
        List<SysRoleMenu> roleMenuList = new ArrayList<>();
        String[] array = meanIds.split(",");
        for(int i=0;i<array.length;i++){
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(Long.valueOf(array[i]));
            sysRoleMenu.setRoleId(1L);
            roleMenuList.add(sysRoleMenu);
        }
        sysRoleMenuMapper.batchRoleMenu(roleMenuList);
        return Result.OK;
    }

}
