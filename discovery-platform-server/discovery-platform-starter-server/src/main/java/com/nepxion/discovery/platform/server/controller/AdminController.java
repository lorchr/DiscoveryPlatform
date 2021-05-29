package com.nepxion.discovery.platform.server.controller;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 *
 * @author Ning Zhang
 * @version 1.0
 */

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nepxion.discovery.platform.server.adapter.PlatformLoginAdapter;
import com.nepxion.discovery.platform.server.constant.PlatformConstant;
import com.nepxion.discovery.platform.server.entity.dto.SysAdminDto;
import com.nepxion.discovery.platform.server.entity.enums.LoginMode;
import com.nepxion.discovery.platform.server.entity.response.Result;
import com.nepxion.discovery.platform.server.entity.vo.AdminVo;
import com.nepxion.discovery.platform.server.exception.BusinessException;
import com.nepxion.discovery.platform.server.service.AdminService;
import com.nepxion.discovery.platform.server.service.RoleService;
import com.nepxion.discovery.platform.server.tool.CommonTool;

@Controller
@RequestMapping(AdminController.PREFIX)
public class AdminController {
    public static final String PREFIX = "admin";

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PlatformLoginAdapter loginAdapter;

    @GetMapping("list")
    public String list() {
        return String.format("%s/%s", PREFIX, "list");
    }

    @GetMapping("add")
    public String add(Model model) throws Exception {
        model.addAttribute("roles", roleService.listOrderByName());

        if (loginAdapter.getLoginMode() == LoginMode.DATABASE) {
            return String.format("%s/%s", PREFIX, "add");
        }
        if (loginAdapter.getLoginMode() == LoginMode.LDAP) {
            return String.format("%s/%s", PREFIX, "addldap");
        }
        throw new BusinessException(String.format("暂不支持登录模式[%s]", loginAdapter.getLoginMode()));
    }

    @GetMapping("edit")
    public String edit(Model model, @RequestParam(name = "id") Long id) throws Exception {
        model.addAttribute("admin", adminService.getById(id));
        model.addAttribute("roles", roleService.listOrderByName());
        model.addAttribute("loginMode", loginAdapter.getLoginMode());
        return String.format("%s/%s", PREFIX, "edit");
    }

    @PostMapping("do-list")
    @ResponseBody
    public Result<List<AdminVo>> doList(@RequestParam(value = "name", required = false) String name, @RequestParam(value = "page") Integer pageNum, @RequestParam(value = "limit") Integer pageSize) throws Exception {
        IPage<AdminVo> adminPage = adminService.list(loginAdapter.getLoginMode(), name, pageNum, pageSize);
        return Result.ok(adminPage.getRecords(), adminPage.getTotal());
    }

    @PostMapping("do-reset-password")
    @ResponseBody
    public Result<?> doResetPassword(@RequestParam(value = "id") Long id) throws Exception {
        SysAdminDto sysAdmin = adminService.getById(id);
        if (sysAdmin == null) {
            return Result.error(String.format("用户[id=%s]不存在", id));
        }
        if (adminService.changePassword(id, sysAdmin.getPassword(), CommonTool.hash(PlatformConstant.DEFAULT_ADMIN_PASSWORD))) {
            return Result.ok();
        } else {
            return Result.error("密码修改失败");
        }
    }

    @PostMapping("do-add")
    @ResponseBody
    public Result<?> doAdd(@RequestParam(value = "roleId") Long roleId, @RequestParam(value = "username") String username, @RequestParam(value = "password", defaultValue = "") String password, @RequestParam(value = "name") String name, @RequestParam(value = "phoneNumber") String phoneNumber, @RequestParam(value = "email") String email, @RequestParam(value = "remark") String remark) throws Exception {
        adminService.insert(loginAdapter.getLoginMode(), roleId, username, password, name, phoneNumber, email, remark);
        return Result.ok();
    }

    @PostMapping("do-edit")
    @ResponseBody
    public Result<?> doEdit(@RequestParam(value = "id") Long id, @RequestParam(value = "roleId") Long roleId, @RequestParam(value = "username") String username, @RequestParam(value = "name") String name, @RequestParam(value = "phoneNumber") String phoneNumber, @RequestParam(value = "email") String email, @RequestParam(value = "remark") String remark) throws Exception {
        adminService.update(id, roleId, username, name, phoneNumber, email, remark);
        return Result.ok();
    }

    @PostMapping("do-delete")
    @ResponseBody
    public Result<?> doDelete(@RequestParam(value = "ids") String ids) {
        List<Long> idList = CommonTool.parseList(ids, ",", Long.class);
        adminService.removeByIds(new HashSet<>(idList));
        return Result.ok();
    }

    @PostMapping("do-search")
    @ResponseBody
    public Result<List<AdminVo>> doSearch(@RequestParam(value = "keyword", defaultValue = "") String keyword) {
        if (StringUtils.isEmpty(keyword.trim())) {
            return Result.ok();
        }
        return Result.ok(adminService.search(keyword, 0, 10));
    }
}