package life.majiang.community.community.controller;

import life.majiang.community.community.provider.GithubProvider;
import life.majiang.community.community.dto.AcceeTokenDTO;
import life.majiang.community.community.dto.GithubUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {

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
                           @RequestParam(name = "state") String state){
        AcceeTokenDTO acceeTokenDTO = new AcceeTokenDTO();
        acceeTokenDTO.setCode(code);
        acceeTokenDTO.setClient_id(clientId);
        acceeTokenDTO.setRedirect_uri(redirectUri);
        acceeTokenDTO.setState(state);
        acceeTokenDTO.setClient_secret(clientSecret);
        String accessToken = githubProvider.getAccessToken(acceeTokenDTO);
        GithubUser user=githubProvider.getUser(accessToken);
        System.out.println(user.getName());
        return "index";
    }
}
