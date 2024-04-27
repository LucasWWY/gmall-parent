package com.example.gmall.search.vo;

import com.example.gmall.search.Goods;
import lombok.Data;

import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 6:05 am
 * @Description
 */
@Data
public class SearchRespVO {

    //web-all中前端页面读取的对象

    //1、检索用的所有参数
    private SearchParamVO  searchParam;

    //2、品牌面包屑（返回用户选择的品牌）
    private String trademarkParam;

    //3、属性面包屑（返回用户选择的几个平台属性）
    private List<Props> propsParamList;

    //4、品牌列表
    private List<Trademark> trademarkList;

    //5、属性列表
    private List<Attrs>  attrsList;

    //6、url参数
    private String urlParam;

    //7、排序
    private OrderMap  orderMap;

    //TODO 8、商品集合; 商品数据原来是在MySQL中；需要通过上架操作给es存一份
    private List<Goods> goodsList;

    //9、页码
    private Integer pageNo;

    //10、总页码
    private Long totalPages;


    @Data
    public static class Props {
        private String attrName;
        private String attrValue;
        private Long attrId;
    }

    @Data
    public static class Trademark{
        private Long tmId;
        private String tmName;
        private String tmLogoUrl;
    }

    @Data
    public static class Attrs{
        private String attrName;
        private List<String> attrValueList;
        private Long attrId;
    }

    @Data
    public static class OrderMap {
        private String type;
        private String sort;
    }

}
