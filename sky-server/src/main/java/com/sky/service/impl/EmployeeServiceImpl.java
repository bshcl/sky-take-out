package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工信息
     * @param employeeDTO
     */
    public void addEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //把DTO和实体类重复的属性copy到新建的对象里
        BeanUtils.copyProperties(employeeDTO, employee);
        //设置状态 1:启用 0:禁用 默认为1
        employee.setStatus(1);
        //设置初始密码 默认:123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置创建时间
        //employee.setCreateTime(LocalDateTime.now());
        //设置更新时间
        //employee.setUpdateTime(LocalDateTime.now());
        //把线程对象中获取到员工ID插入到数据库中
        //employee.setCreateUser(BaseContext.getCurrentId());
        //employee.setUpdateUser(BaseContext.getCurrentId());
        //插入到数据库
        employeeMapper.addEmployee(employee);
    }

    /**
     * 分页查询员工信息
     * @param employeePageQueryDTO
     * @return
     */
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page= employeeMapper.pageQuery(employeePageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 修改员工状态 1:启用 0:禁用
     * @param status
     * @param id
     */
    public void startOrStopEmployee(Integer status, Long id) {
        //使用构造器创建一个对象
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 根据ID查询员工信息
     * @param id
     * @return
     */
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("*****");
        return employee;
    }

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }

}
