package com.tsang;

import com.jayway.jsonpath.JsonPath;
import com.tsang.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 集成测试：启动真实的Spring上下文，跑在H2内存库上
 *
 * 覆盖单元测试和standalone MockMvc覆盖不到的部分：
 * 配置绑定与启动期校验、profile装配、拦截器注册、
 * schema.sql/data.sql真的执行成功、以及真实的权限查询链路
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ApplicationIntegrationTest {

    @Autowired
    private Environment environment;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private MockMvc mockMvc;

    /**
     * 保证测试跑在H2上，不会意外连到本机MySQL
     */
    @Test
    void 测试使用H2内存库() {
        String url = environment.getProperty("spring.datasource.url");

        assertThat(url).startsWith("jdbc:h2:");
    }

    /**
     * schema.sql与data.sql确实执行成功，且权限查询链路给出正确结果
     */
    @Test
    void 种子数据与权限查询链路正确() {
        // admin拥有全部四个权限
        assertThat(permissionService.getPermissionCodes(100))
                .containsExactlyInAnyOrder(
                        "user:list",
                        "user:add",
                        "user:update",
                        "user:delete"
                );

        // user只有查询权限
        assertThat(permissionService.getPermissionCodes(101)).containsExactly("user:list");

        // 没有角色的用户返回空列表
        assertThat(permissionService.getPermissionCodes(999)).isEmpty();
    }

    /**
     * 种子密文确实能匹配明文123456，并走通登录+鉴权+分页查询全链路
     */
    @Test
    void 种子账号可登录并访问受保护接口() throws Exception {
        String token = login("admin", "123456");

        mockMvc.perform(get("/page")
                        .param("page", "1")
                        .param("size", "10")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    /**
     * 普通用户只有查询权限，删除接口应返回403
     */
    @Test
    void 普通用户删除接口返回403() throws Exception {
        String token = login("user", "123456");

        mockMvc.perform(delete("/delete")
                        .param("id", "100")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    /**
     * 真实上下文下登录失败返回HTTP 401
     */
    @Test
    void 登录失败返回401() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(401));
    }

    /**
     * 访问不存在的路径返回404，而不是被兜底处理器报成"系统异常"
     *
     * 注：需要带合法Token，否则拦截器先返回401（鉴权在路由之前）
     */
    @Test
    void 访问不存在的路径返回404() throws Exception {
        String token = login("admin", "123456");

        mockMvc.perform(get("/findAll")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    /**
     * 请求体不是合法JSON时返回400，而不是"系统异常"
     */
    @Test
    void 请求体格式错误返回400() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    /**
     * 按ID查询不存在的用户返回404（业务异常携带状态码）
     */
    @Test
    void 查询不存在的用户返回404() throws Exception {
        String token = login("admin", "123456");

        mockMvc.perform(get("/find")
                        .param("id", "999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    /**
     * 新增已存在的登录账号返回409（先查重拒绝，不依赖唯一索引）
     */
    @Test
    void 新增已存在的账号返回409() throws Exception {
        String token = login("admin", "123456");

        mockMvc.perform(post("/add")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                                {"name":"重复账号","age":20,"username":"admin","password":"123456"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("登录账号已存在"));
    }

    /**
     * 登录并返回Token
     */
    private String login(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        return JsonPath.<String>read(body, "$.data.token");
    }
}
