package com.example.wenda.controller;



import com.example.wenda.aspect.LogAspect;
import com.example.wenda.model.User;
import com.example.wenda.service.WendaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

//@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger( IndexController.class);
    @Autowired
    WendaService wendaService;
    @RequestMapping(path={"/","/index"},method= RequestMethod.GET)
    @ResponseBody
    public String index(HttpSession httpSession)
    {   logger.info("VISIT HOME");
        return wendaService.getMessage(2)+"hello nowCoder"+httpSession.getAttribute("msg");
    }

    @RequestMapping(path={"/profile/{groupId}/{userId}"})
    @ResponseBody
   public String profile(@PathVariable("userId")int userId,
                       @PathVariable("groupId") String groupId,
                       @RequestParam(value="type",defaultValue="1")int type,
                       @RequestParam(value="key",defaultValue="nowcoder",required=false)String key)
   {
       return String.format("Profile Pages of %s/%d,t: %d,k: %s",groupId,userId,type,key);
   }

    @RequestMapping(path={"/vm"},method= RequestMethod.GET)
    public String template(Model model)
    {
        model.addAttribute("value1","vvvvvl");
        List<String> colors= Arrays.asList(new String[]{"RED","BLUE","GREEN"});
        model.addAttribute("colors",colors);
        HashMap<String,String>map=new HashMap<>();
        for (int i = 0; i <3 ; i++) {
            map.put(String.valueOf(i),String.valueOf(i*i));

        }
        model.addAttribute("map",map);
        model.addAttribute("user",new User("Lee"));
        return "home";
    }

    @RequestMapping(path={"/request"},method = RequestMethod.GET)
    @ResponseBody
    public String template(Model model, HttpServletRequest request, HttpSession httpSession, HttpServletResponse response,
    @CookieValue("JSESSIONID") String sessionId)
    {
         StringBuilder sb=new StringBuilder();
         sb.append("CookieValue:"+sessionId);
        Enumeration<String> headerNames=request.getHeaderNames();
        while(headerNames.hasMoreElements())
        {
            String name=headerNames.nextElement();
            sb.append(name+":"+request.getHeader(name)+"</br>");
        }
        if(request.getCookies()!=null)
        {
            for(Cookie cookie:request.getCookies())
             sb.append("Cookie:"+cookie.getName()+"value"+cookie.getValue());

        }
         sb.append(request.getMethod()+"</br>");
         sb.append(request.getQueryString()+"</br>");
         sb.append(request.getPathInfo()+"</br>");
         sb.append(request.getRequestURI()+"</br>");
         response.addHeader("nowCoderId","hello");
         response.addCookie(new Cookie("username","nowCoder"));
         return sb.toString();
    }

    @RequestMapping(path={"/redirect/{code}"},method=RequestMethod.GET)
    public RedirectView redirect(@PathVariable ("code") int code, HttpSession httpSession)
    {
        httpSession.setAttribute("msg","jump from redirect ");
         RedirectView red=new RedirectView("/",true);
         if(code==301)
         {
             red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
         }
         return red;
    }
    @RequestMapping(path={"/admin"},method=RequestMethod.GET)
    @ResponseBody
    public String admin(@RequestParam("key")String key)
    {
        if(key.equals("admin"))
            return "hello admin";
        else
          throw new IllegalArgumentException("参数不对");
    }
    @ExceptionHandler
    @ResponseBody
    public String error(Exception e)
    {
        return "error"+e.getMessage();
    }




}
