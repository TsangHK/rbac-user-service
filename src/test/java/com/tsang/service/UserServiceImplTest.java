package com.tsang.service;

import com.tsang.entity.User;
import com.tsang.exception.BusinessException;
import com.tsang.mapper.UserMapper;
import com.tsang.utils.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 用户业务测试（Mock数据库层）
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void 登录成功返回用户() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword(PasswordUtils.encode("123456"));

        when(userMapper.selectOne(any())).thenReturn(user);

        User result = userService.login("admin", "123456");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("admin");
    }

    @Test
    void 用户不存在时登录失败() {
        when(userMapper.selectOne(any())).thenReturn(null);

        assertThat(userService.login("nobody", "123456")).isNull();
    }

    @Test
    void 密码错误时登录失败() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword(PasswordUtils.encode("123456"));

        when(userMapper.selectOne(any())).thenReturn(user);

        assertThat(userService.login("admin", "wrong")).isNull();
    }

    @Test
    void 新增用户时密码加密入库() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User user = new User();
        user.setPassword("123456");

        userService.add(user);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());

        // 入库的是密文，且能和明文匹配
        assertThat(captor.getValue().getPassword())
                .isNotEqualTo("123456");

        assertThat(PasswordUtils.matches(
                "123456",
                captor.getValue().getPassword()))
                .isTrue();
    }

    /**
     * 账号重复时直接拒绝
     */
    @Test
    void 新增用户时账号重复被拒绝() {
        when(userMapper.selectCount(any())).thenReturn(1L);

        User user = new User();
        user.setUsername("admin");
        user.setPassword("123456");

        assertThatThrownBy(() -> userService.add(user))
                .isInstanceOf(BusinessException.class)
                .hasMessage("登录账号已存在");

        // 重复账号不应该入库
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void 修改用户时不更新密码() {
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        User user = new User();
        user.setId(1);
        user.setPassword("should-not-save");

        userService.update(user);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());

        // 密码被置空，MyBatis Plus会跳过该列
        assertThat(captor.getValue().getPassword()).isNull();
    }
}
