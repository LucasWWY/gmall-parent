package com.example.gmall.search;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 6:31 am
 * @Description
 */
// Index = goods , Type = info  es 7.8.0 逐渐淡化type！  修改！
@Data
@Document(indexName = "goods" , shards = 3, replicas = 2) //项目一启动，自动创建index，数据从哪里来？skuInfoServiceImpl(goods) 索引通过@Document(indexName = "goods" , shards = 3, replicas = 2)已自动创建 -> searchFeignClient (common) -> searchRpcController (service-search) -> searchServiceImpl -> goodsRepository.save(goods);
public class Goods {

    // 商品Id skuId
    @Id
    private Long id;

    //默认图片 所有文本默认两个类型；
    //Keyword（不用分词：让es底层存储的时候）
    //Text（可分词，可以进行全文搜索）
    //index=false: 不用为这个字段建立倒排索引
    @Field(type = FieldType.Keyword, index = false)
    private String defaultImg;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title; //支持全文检索
    //ES默认会把所有的字符串都当做是Text

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Field(type = FieldType.Long)
    private Long tmId; //trademark

    @Field(type = FieldType.Keyword)
    private String tmName; //小米

    @Field(type = FieldType.Keyword)
    private String tmLogoUrl;
    //以上品牌信息

    @Field(type = FieldType.Long)
    private Long category1Id;

    @Field(type = FieldType.Keyword)
    private String category1Name;

    @Field(type = FieldType.Long)
    private Long category2Id;

    @Field(type = FieldType.Keyword)
    private String category2Name;

    @Field(type = FieldType.Long)
    private Long category3Id;

    @Field(type = FieldType.Keyword)  //游戏手机  拍照手机
    private String category3Name;
    //商品的精确分类信息

    //商品的热度 商品被用户点查看的次数越多 则说明热度就越高
    @Field(type = FieldType.Long)
    private Long hotScore = 0L;

    //平台属性集合对象
    //Nested支持嵌套查询
    //如果文档中有数组或List类型的属性。而且需要对某属性进行检索就必须声明其为nested类型 并使用Nested进行检索
    @Field(type = FieldType.Nested)
    private List<SearchAttr> attrs;

}
