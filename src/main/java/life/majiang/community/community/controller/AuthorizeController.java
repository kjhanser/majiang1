package life.majiang.community.community.controller;

import life.majiang.community.community.mapper.UserMapper;
import life.majiang.community.community.model.User;
import life.majiang.community.community.provider.GithubProvider;
import life.majiang.community.community.dto.AcceeTokenDTO;
import life.majiang.community.community.dto.GithubUser;
import life.majiang.community.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private UserService userService;

    @Autowired
    private GithubProvider githubProvider;



    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;



    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AcceeTokenDTO acceeTokenDTO = new AcceeTokenDTO();
        acceeTokenDTO.setCode(code);
        acceeTokenDTO.setClient_id(clientId);
        acceeTokenDTO.setRedirect_uri(redirectUri);
        acceeTokenDTO.setState(state);
        acceeTokenDTO.setClient_secret(clientSecret);
        String accessToken = githubProvider.getAccessToken(acceeTokenDTO);
        System.out.println(accessToken);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if (githubUser!= null && githubUser.getId() !=null){
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatar_url());
            userService.createOrupdate(user);
            response.addCookie(new Cookie("token",token));
            System.out.println("登录成功");
            return "redirect:/";
            //登录成功，写cookie 和session
        }else {
            System.out.println("登录失败");
            return "redirect:/";
            //登录失败，重新登录
        }
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie=new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
