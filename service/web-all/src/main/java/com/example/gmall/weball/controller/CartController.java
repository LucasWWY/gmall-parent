package com.example.gmall.weball.controller;

import com.example.gmall.cart.vo.AddCartSuccessVO;
import com.example.gmall.common.result.Result;
import com.example.gmall.feign.cart.CartFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 20/6/2024 - 10:16 pm
 * @Description
 */
@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    //addToCart()执行期间注意不能只有放不删，这个map是static的，只要weball app一直启动就会无限膨胀最终OOM
    //public static final Map<Thread, HttpServletRequest> requestMap = new ConcurrentHashMap<>();
    //final关键字并不影响对象本身的可变性，而只是确保引用本身不可变，即不能改变这个引用指向的这个对象

    //Java已经写好了同一个线程共享数据用的，key就是Thread.currentThread()
    //public static final ThreadLocal<HttpServletRequest> threadLocal = new ThreadLocal<>();

    //但SpringMVC也有RequestContextHolder实现线程绑定（底层也是ThreadLocal)，tomcat一旦收到请求，就会触发ServletRequestListener的requestInitialized()方法，这个方法会把request放到RequestContextHolder


    @GetMapping("/addCart.html")
    public String addToCart(@RequestParam("skuId") Long skuId,
                          @RequestParam("skuNum") Integer skuNum,
                          //@RequestHeader(value = RedisConst.USER_ID_HEADER, required = false) String userId, //透传userId
                          //@RequestHeader(value = RedisConst.USER_TEMP_ID_HEADER, required = false) String userTempId,
                          //HttpServletRequest request, //有了request就不再需要单独获取userId和userTempId了，都可以从request中拿到
                          Model model) { //传入的是ExtendedModelMap，ExtendedModelMap 实现了Model接口，同时也继承了ModelMap。这使得它不仅可以使用Model接口定义的方法，还可以使用ModelMap的所有方法（包括所有Map的方法）。ExtendedModelMap 提供了一些方便的方法，用于添加和访问模型数据。

        //Tomcat的web-all每次接到一个请求，会分配一个线程来处理整个请求到结束
        //也就是addCart这个方法执行期间（整个远程调用，包含拦截器拦截）一直都是同一个线程
        //基于上面提的线程唯一性，我们可以利用这一点（线程绑定机制：相当于把request绑定到了线程上），
        //在拦截器那里CartController.requestMap.get(Thread.cu rrentThread())获取到request，这样就可以把request的header放到requestTemplate中了
        //threadLocal.set(request); //代替requestMap.put(Thread.currentThread(), request);


        //远程调用cart服务，把商品加入到cart
        //这里还需要userId / userTempId，但是如果在后面每个方法中(e.g. cartFeignClient, cartRpcController)都添加这两个参数，会显得很冗余
        //而cartFeignClient.addToCart(skuId, skuNum) -> cartRpcController.addToCart(skuId, skuNum)这个调用链中，通过断点测试发现丢失了这两个header信息，也就是丢失了请求头
        //我们希望 隐式透传，在weball发起远程调用的时候，把网关透传的放在header的userId/userTempId继续隐式传给目标微服务
        // 所以可以使用拦截器，把userId / userTempId放入到请求头中
        Result<AddCartSuccessVO> result = cartFeignClient.addToCart(skuId, skuNum);

        //为防止OOM，interceptor处把老请求头传递给requestTemplate，这个request使命就结束了，所以要删除
        //threadLocal.remove();//代替requestMap.remove(Thread.currentThread());
        //我们实际上可以放弃threadLocal.remove()
        //请求进来 -> dispatcherServlet -> controller -> service -> dao 都是同一个线程
        //线程绑定发生的越早越好，这样后续所有环节都能直接去threadLocal拿数据
        //SpringMVC已经通过RequestContextListener + RequestContextHolder处理好了，只要开始处理请求触发监听器，自动绑定请求到当前线程
        //直接去interceptor那里：ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes(); 后续任何环节都可以这么得到request

        model.addAttribute("skuInfo", result.getData().getSkuInfo());
        model.addAttribute("skuNum", result.getData().getSkuNum());

        return "cart/addCart";
    }

    /**
     * 购物车列表页
     * 页面渲染：
     * 1、服务端渲染： 服务器负责把页面的全部内容组装完全再返回
     * 2、客户端渲染：
     *   - 客户端给服务器发送ajax请求
     *   - 服务器把数据返给浏览器，客户端把数据填充到这个页面
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(){

        return "cart/index"; //死页，index里面都是固定的东西，是前端通过ajax请求cartApiController得到数据的
    }


    /**
     * 删除选中的商品
     * @return
     */
    @GetMapping("/cart/deleteChecked")
    public String deleteChecked(){

        cartFeignClient.deleteChecked();

        return "redirect:http://cart.gmall.com/cart.html";
    }

}
