package com.tsang;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsang.config.JwtProperties;
import com.tsang.controller.LoginController;
import com.tsang.controller.UserInfoController;
import com.tsang.controller.UserController;
import com.tsang.entity.User;
import com.tsang.exception.BusinessException;
import com.tsang.exception.GlobalExceptionHandler;
import com.tsang.interceptor.JwtInterceptor;
import com.tsang.service.PermissionService;
import com.tsang.service.UserService;
import com.tsang.utils.JwtUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 认证与权限集成测试：登录、Token校验、401/403权限拦截
 */
class AuthFlowTest {

    // 测试密钥（仅测试用）
    private static final String SECRET = "integration-test-secret-key-1234567890";

    private final JwtUtils jwtUtils = new JwtUtils(new JwtProperties(SECRET, 24 * 60 * 60 * 1000));

    private final UserService userService = mock(UserService.class);

    private final PermissionService permissionService = mock(PermissionService.class);

    // 登录接口（不带拦截器）
    private MockMvc openMvc;

    // 受保护接口（带JWT拦截器）
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        openMvc = MockMvcBuilders
                .standaloneSetup(
                        new LoginController(userService, permissionService, jwtUtils)
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mvc = MockMvcBuilders
                .standaloneSetup(
                        new UserController(userService),
                        new UserInfoController(userService)
                )
                .addInterceptors(new JwtInterceptor(jwtUtils))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /**
     * 生成测试Token，permissions为该用户拥有的权限
     */
    private String token(String... permissions) {
        return jwtUtils.createToken(1, "tester", List.of(permissions));
    }

    private User mockUser() {
        User user = new User();
        user.setId(1);
        user.setName("测试用户");
        user.setUsername("admin");
        user.setPassword("encoded");
        return user;
    }

    @Test
    void 登录成功返回Token和权限() throws Exception {
        when(userService.login("admin", "123456")).thenReturn(mockUser());

        when(permissionService.getPermissionCodes(1)).thenReturn(List.of("user:list"));

        openMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.permissions[0]").value("user:list"));
    }

    @Test
    void 登录失败返回401() throws Exception {
        when(userService.login(any(), any())).thenReturn(null);

        openMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    void 未登录访问返回401() throws Exception {
        mvc.perform(get("/page")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void Token无效返回401() throws Exception {
        mvc.perform(get("/page")
                        .param("page", "1")
                        .param("size", "10")
                        .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 无权限访问返回403() throws Exception {
        mvc.perform(get("/page")
                        .param("page", "1")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void 有权限可以访问() throws Exception {
        when(userService.page(any(), any(), any(), any())).thenReturn(new Page<>(1, 10));

        mvc.perform(get("/page")
                        .param("page", "1")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + token("user:list")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void 有删除权限才能删除用户() throws Exception {
        when(userService.delete(2)).thenReturn(true);

        mvc.perform(delete("/delete")
                        .param("id", "2")
                        .header("Authorization", "Bearer " + token("user:delete")))
                .andExpect(status().isOk());
    }

    @Test
    void 没有删除权限返回403() throws Exception {
        mvc.perform(delete("/delete")
                        .param("id", "2")
                        .header("Authorization", "Bearer " + token("user:list")))
                .andExpect(status().isForbidden());
    }

    @Test
    void 当前用户信息不返回密码() throws Exception {
        when(userService.findById(1)).thenReturn(mockUser());

        mvc.perform(get("/user/info")
                        .header("Authorization", "Bearer " + token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void 修改用户缺姓名时参数校验生效() throws Exception {
        mvc.perform(put("/update")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token("user:update"))
                        .content("""
                                {"id":1,"age":20,"username":"admin"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("姓名不能为空"));
    }

    @Test
    void 新增用户时密码能传入() throws Exception {
        when(userService.add(any(User.class))).thenReturn(true);

        mvc.perform(post("/add")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token("user:add"))
                        .content("""
                                {"name":"张三","age":20,"username":"zhangsan","password":"123456"}
                                """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userService).add(captor.capture());

        // 入参里带了密码，反序列化没有把它丢掉
        assertThat(captor.getValue().getPassword()).isEqualTo("123456");

        assertThat(captor.getValue().getUsername()).isEqualTo("zhangsan");
    }

    /**
     * 缺密码时由@NotBlank拦住
     */
    @Test
    void 新增用户缺密码时提示校验信息() throws Exception {
        mvc.perform(post("/add")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token("user:add"))
                        .content("""
                                {"name":"张三","age":20,"username":"zhangsan"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("密码不能为空"));

        verify(userService, never()).add(any(User.class));
    }

    /**
     * 分页参数越界时返回提示，不放行超大查询
     */
    @Test
    void 分页条数超上限被拦截() throws Exception {
        mvc.perform(get("/page")
                        .param("page", "1")
                        .param("size", "100000000")
                        .header("Authorization", "Bearer " + token("user:list")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("每页条数不能超过100"));

        verify(userService, never()).page(any(), any(), any(), any());
    }

    /**
     * 库里存在重复账号时返回明确提示
     *
     * 这是库里的数据异常，属于服务端问题，返回500
     */
    @Test
    void 登录时账号重复返回明确提示() throws Exception {
        when(userService.login(any(), any())).thenThrow(new TooManyResultsException("found: 2"));

        openMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"123456"}
                                """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(
                        "账号数据异常：存在重复的登录账号，请联系管理员处理"));
    }

    /**
     * 用户不存在属于资源不存在，返回404
     */
    @Test
    void 当前用户不存在时返回404() throws Exception {
        when(userService.findById(1)).thenReturn(null);

        mvc.perform(get("/user/info")
                        .header("Authorization", "Bearer " + token()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在或已被删除"));
    }

    /**
     * 新增时账号已存在属于数据冲突，返回409
     */
    @Test
    void 新增用户账号已存在返回409() throws Exception {
        when(userService.add(any(User.class)))
                .thenThrow(new BusinessException(HttpStatus.CONFLICT, "登录账号已存在"));

        mvc.perform(post("/add")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token("user:add"))
                        .content("""
                                {"name":"张三","age":20,"username":"admin","password":"123456"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("登录账号已存在"));
    }
}
