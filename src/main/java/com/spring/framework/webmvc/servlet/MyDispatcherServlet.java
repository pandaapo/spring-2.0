package com.spring.framework.webmvc.servlet;

import com.spring.framework.annotation.MyController;
import com.spring.framework.annotation.MyRequestMapping;
import com.spring.framework.context.MyApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 开始MVC的地方
 */
@Slf4j
public class MyDispatcherServlet extends HttpServlet {
    private MyApplicationContext context;

    //一个常量
    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private List<MyHandlerMapping> handlerMappings = new ArrayList<MyHandlerMapping>();

    private Map<MyHandlerMapping, MyHandlerAdapter> handlerAdapters = new HashMap<MyHandlerMapping, MyHandlerAdapter>();

    private List<MyViewResolver> viewResolvers = new ArrayList<MyViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDisPatch(req, resp);
        } catch (Exception e) {
            //500
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replace("\\[|\\]", "").replace("\\s","\r\n"));
            e.printStackTrace();
        }
    }

    private void doDisPatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        MyHandlerMapping handlerMapping = getHandler(req);
        if(handlerMapping == null) {
            processDispatchResult(req, resp, new MyModelAndView("404"));
            return;
        }
        //2、获取处理request的处理器适配器：准备好调用前的参数
        MyHandlerAdapter ha = getHandlerAdapter(handlerMapping);
        //3、真正的调用方法，返回ModelAndView。源码中下面入参的handler包装了Method。ModelAndView存储了要传到页面上的值，和页面模板的名称
        MyModelAndView mv = ha.handle(req, resp, handlerMapping);
        //把ModelView解析成html/outputstream/json/freemark……
        processDispatchResult(req, resp, mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MyModelAndView mv) throws Exception{
        if(null == mv) {return;}
        if(this.viewResolvers.isEmpty()) {return;}
        for (MyViewResolver viewResolver : this.viewResolvers) {
            MyView view = viewResolver.resolveViewName(mv.getViewName(),null);
            view.render(mv.getModel(), req, resp);
            return;
        }

    }

    private MyHandlerAdapter getHandlerAdapter(MyHandlerMapping handlerMapping) {
        if(this.handlerAdapters.isEmpty()) {return null;}
        MyHandlerAdapter ha = this.handlerAdapters.get(handlerMapping);
        if(ha.supports(handlerMapping)){
            return ha;
        }
        return null;
    }

    private MyHandlerMapping getHandler(HttpServletRequest request) throws Exception {
        if(this.handlerMappings.isEmpty()){
            return null;
        }
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");
        for (MyHandlerMapping handler : this.handlerMappings) {
            try {
                Matcher matcher = handler.getPattern().matcher(url);
                if(!matcher.matches()){
                    continue;
                }
                return handler;
            }catch (Exception e) {
                throw e;
            }
        }
        return null;
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
       //1、初始化IOC工厂：ApplicationContext
        context = new MyApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2、初始化Spring MVC的九大组件
        initStrategies(context);
    }

    //初始化策略
    protected void initStrategies(MyApplicationContext context) {
        initMultipartResolver(context);
        initLocaleResolver(context);
        initThemeResolver(context);
        //重点
        initHandlerMappings(context);
        //重点
        initHandlerAdapters(context);
        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context);
        //重点
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(MyApplicationContext context) {
    }

    //初始化视图转换器
    private void initViewResolvers(MyApplicationContext context) {
        //通过配置文件获取模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        //解析成全局的路径
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File temlateRootDir = new File(templateRootPath);
        for (File file : temlateRootDir.listFiles()) {
            this.viewResolvers.add(new MyViewResolver(templateRoot));
        }

    }

    //初始化视图预处理器
    private void initRequestToViewNameTranslator(MyApplicationContext context) {
    }

    //初始化异常拦截器
    private void initHandlerExceptionResolvers(MyApplicationContext context) {
    }

    //初始化参数适配器：把一个request请求变成一个handler，参数都是字符串的，需要自动匹配到handler中的形参。所以要拿到handlerMapping才能干活。HandlerMapping和HandlerAdapter个数相等
    private void initHandlerAdapters(MyApplicationContext context) {
        for (MyHandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new MyHandlerAdapter());
        }

    }

    private void initHandlerMappings(MyApplicationContext context) {
        /** 把容器中所有对象取出来遍历，找出controller **/
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                /** 判断是不是Controller **/
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if(!clazz.isAnnotationPresent(MyController.class)){
                    continue;
                }
                /** 判断是不是RequestMapping **/
                String baseUrl = "";
                //获取Controller的url配置
                if(clazz.isAnnotationPresent(MyRequestMapping.class)){
                    MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                /** 获取Method的url配置 **/
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    //没有加RequestMapping注解的直接忽略
                    if(!method.isAnnotationPresent(MyRequestMapping.class)){
                        continue;
                    }
                    //映射url
                    MyRequestMapping requestMapping = method.getAnnotation(MyRequestMapping.class);
                    String regex = ("/" +baseUrl + "/" + requestMapping.value()).replaceAll("\\*",".*").replaceAll("/+","/");
                    Pattern pattern = Pattern.compile(regex);

                    this.handlerMappings.add(new MyHandlerMapping(pattern, controller, method));
                    log.info("Mapped" +regex+ "," +method);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //初始化模板处理器
    private void initThemeResolver(MyApplicationContext context) {
    }

    //初始化本地语言环境
    private void initLocaleResolver(MyApplicationContext context) {
    }

    //多文件上传的组件
    private void initMultipartResolver(MyApplicationContext context) {
    }
}
