package com.nepxion.discovery.platform.mysql.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nepxion.discovery.common.entity.dto.SysAdmin;
import com.nepxion.discovery.common.entity.enums.LoginMode;
import com.nepxion.discovery.common.entity.vo.Admin;
import com.nepxion.discovery.common.interfaces.AdminService;
import com.nepxion.discovery.common.interfaces.RoleService;
import com.nepxion.discovery.platform.mysql.mapper.DbAdminMapper;
import com.nepxion.discovery.platform.tool.anno.TranRead;
import com.nepxion.discovery.platform.tool.anno.TranSave;
import com.nepxion.discovery.platform.tool.common.CommonTool;
import com.nepxion.discovery.platform.tool.exception.BusinessException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 *
 * @author Ning Zhang
 * @version 1.0
 */

@Service
public class DbAdminService extends ServiceImpl<DbAdminMapper, SysAdmin> implements AdminService {
    @Autowired
    private RoleService roleService;

    @TranRead
    @Override
    public boolean authenticate(final String username,
                                final String password) throws Exception {
        if (ObjectUtils.isEmpty(username) || ObjectUtils.isEmpty(password)) {
            return false;
        }
        final SysAdmin sysAdmin = this.getByUserName(username);
        if (null == sysAdmin) {
            return false;
        }

        return password.equals(sysAdmin.getPassword());
    }

    @TranRead
    @Override
    public Admin getAdminByUserName(final String username) throws Exception {
        final SysAdmin sysAdmin = this.getByUserName(username);
        if (null == sysAdmin) {
            return null;
        }

        final Admin admin = new Admin();
        BeanUtils.copyProperties(admin, sysAdmin);
        admin.setSysRole(this.roleService.getById(admin.getSysRoleId()));
        return admin;
    }

    @TranRead
    @Override
    public SysAdmin getByUserName(final String username) {
        final QueryWrapper<SysAdmin> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysAdmin::getUsername, username);
        return this.getOne(queryWrapper);
    }

    @TranSave
    @Override
    public boolean changePassword(final Long id,
                                  final String oldPassword,
                                  final String newPassword) {
        final SysAdmin sysAdmin = this.getById(id);
        if (null == sysAdmin) {
            return false;
        }

        if (!sysAdmin.getPassword().equals(oldPassword)) {
            throw new BusinessException("密码不匹配, 修改密码失败");
        }

        UpdateWrapper<SysAdmin> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(SysAdmin::getId, id)
                .set(SysAdmin::getPassword, newPassword);
        return this.update(updateWrapper);
    }

    @TranSave
    @Override
    public boolean insert(final LoginMode loginMode,
                          final Long roleId,
                          final String username,
                          final String password,
                          final String name,
                          final String phoneNumber,
                          final String email,
                          final String remark) {
        SysAdmin sysAdmin = this.getByUserName(username);
        if (null != sysAdmin) {
            throw new BusinessException(String.format("用户名[%s]已存在", username));
        }

        sysAdmin = new SysAdmin();
        sysAdmin.setLoginMode(loginMode.getCode());
        sysAdmin.setSysRoleId(roleId);
        sysAdmin.setUsername(username);
        sysAdmin.setPassword(password);
        sysAdmin.setName(name);
        sysAdmin.setPhoneNumber(phoneNumber);
        sysAdmin.setEmail(email);
        sysAdmin.setRemark(remark);
        return this.save(sysAdmin);
    }

    @TranSave
    @Override
    public boolean update(final Long id,
                          Long roleId,
                          String username,
                          String name,
                          String phoneNumber,
                          String email,
                          String remark) {
        final SysAdmin sysAdmin = this.getById(id);
        if (null == sysAdmin) {
            return false;
        }

        final UpdateWrapper<SysAdmin> updateWrapper = new UpdateWrapper<>();
        final LambdaUpdateWrapper<SysAdmin> lambdaUpdateWrapper = updateWrapper.lambda().eq(SysAdmin::getId, id);

        if (null != roleId) {
            lambdaUpdateWrapper.set(SysAdmin::getSysRoleId, roleId);
        }
        if (null != username) {
            lambdaUpdateWrapper.set(SysAdmin::getUsername, username);
        }
        if (null != name) {
            lambdaUpdateWrapper.set(SysAdmin::getName, name);
        }
        if (null != phoneNumber) {
            lambdaUpdateWrapper.set(SysAdmin::getPhoneNumber, phoneNumber);
        }
        if (null != email) {
            lambdaUpdateWrapper.set(SysAdmin::getEmail, email);
        }
        if (null != remark) {
            lambdaUpdateWrapper.set(SysAdmin::getRemark, remark);
        }
        return this.update(updateWrapper);
    }

    @TranRead
    @Override
    public IPage<Admin> list(final LoginMode loginMode,
                             final String name,
                             final Integer pageNum,
                             final Integer pageSize) {
        return this.baseMapper.list(new Page<>(pageNum, pageSize), loginMode.getCode(), name);
    }

    @TranRead
    @Override
    public List<Admin> search(final String keyword,
                              final Integer pageNum,
                              final Integer pageSize) {
        final QueryWrapper<SysAdmin> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(SysAdmin::getName, keyword);
        final Page<SysAdmin> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        return CommonTool.toVoList(page.getRecords(), Admin.class);
    }

    @TranRead
    @Override
    public List<SysAdmin> getByRoleId(final Long roleId) {
        final QueryWrapper<SysAdmin> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysAdmin::getSysRoleId, roleId);
        return this.list(queryWrapper);
    }

    @TranRead
    @Override
    public SysAdmin getById(final Long id) {
        return super.getById(id);
    }

    @TranSave
    @Override
    public boolean removeByIds(Set<Long> idList) {
        return super.removeByIds(idList);
    }
}