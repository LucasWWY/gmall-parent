package com.example.gmall.search.service.impl;

import com.example.gmall.search.Goods;
import com.example.gmall.search.repo.GoodsRepository;
import com.example.gmall.search.service.SearchService;
import com.example.gmall.search.vo.SearchParamVO;
import com.example.gmall.search.vo.SearchRespVO;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 6:26 am
 * @Description
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    ElasticsearchRestTemplate elasticsearchRestTemplate;

    int pageSize = 10;

    @Override
    public SearchRespVO search(SearchParamVO searchParamVO) {
        //1. 解析searchParamVO中封装的url的参数 构建检索条件
        Query query = getQuery(searchParamVO);

        SearchHits<Goods> result = elasticsearchRestTemplate.search(query, Goods.class, IndexCoordinates.of("goods"));

        SearchRespVO respVO = buildSearchResp(result,searchParamVO);

        return respVO;
    }

    //根据searchParamVO检索到的结果，构建前端需要的返回结果SearchRespVO
    private SearchRespVO buildSearchResp(SearchHits<Goods> result, SearchParamVO searchParamVO) {
        SearchRespVO respVO = new SearchRespVO();
        //1、检索参数
        respVO.setSearchParam(searchParamVO);

        //2、品牌面包屑 [x 品牌：apple]
        if(!StringUtils.isEmpty(searchParamVO.getTrademark())){
            respVO.setTrademarkParam("品牌："+searchParamVO.getTrademark().split(":")[1]);
        }

        //3、属性面包屑
        if (searchParamVO.getProps()!=null && searchParamVO.getProps().length>0) {
            //遍历前端传递的所有props参数
            List<SearchRespVO.Props> collect = Arrays.stream(searchParamVO.getProps())
                    .map(item -> {
                        String[] spilt = item.split(":");
                        SearchRespVO.Props props = new SearchRespVO.Props();
                        props.setAttrName(spilt[2]);
                        props.setAttrValue(spilt[1]);
                        props.setAttrId(Long.parseLong(spilt[0]));
                        return props;
                    }).collect(Collectors.toList());
            respVO.setPropsParamList(collect);
        }

        //4、品牌列表;  聚合 aggregations：
        // ParsedTerms + 属性类型Long = ParsedLongTerms
        ParsedLongTerms tmIdAgg = result.getAggregations().get("tmIdAgg");

        List<SearchRespVO.Trademark> trademarks = tmIdAgg.getBuckets().stream()
                .map(bucket -> {
                    SearchRespVO.Trademark trademark = new SearchRespVO.Trademark();

                    //1、品牌id
                    long tmId = bucket.getKeyAsNumber().longValue();
                    trademark.setTmId(tmId);

                    //2、品牌Name
                    ParsedStringTerms tmNameAgg = bucket.getAggregations().get("tmNameAgg");
                    String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
                    trademark.setTmName(tmName);

                    //3、品牌logo
                    ParsedStringTerms tmLogoAgg = bucket.getAggregations().get("tmLogoAgg");
                    String tmLogo = tmLogoAgg.getBuckets().get(0).getKeyAsString();
                    trademark.setTmLogoUrl(tmLogo);

                    return trademark;
                }).collect(Collectors.toList());

        respVO.setTrademarkList(trademarks);


        //5、平台属性列表； aggregations select GROUP_CONCAT(attr_value) group by attrId
        ParsedNested attrAgg = result.getAggregations().get("attrAgg");
        //平台属性按id聚合
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<SearchRespVO.Attrs> attrsList = attrIdAgg.getBuckets()
                .stream()
                .map(bucket -> {
                    SearchRespVO.Attrs attrs = new SearchRespVO.Attrs();

                    //平台属性id
                    long attrId = bucket.getKeyAsNumber().longValue();
                    attrs.setAttrId(attrId);

                    //平台属性名
                    ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
                    String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
                    attrs.setAttrName(attrName);

                    //平台属性值
                    ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
                    List<String> valueList = attrValueAgg.getBuckets()
                            .stream()
                            .map(item -> item.getKeyAsString())
                            .collect(Collectors.toList());
                    attrs.setAttrValueList(valueList);
                    return attrs;
                })
                .collect(Collectors.toList());

        respVO.setAttrsList(attrsList);

        //6、url参数 就是这一堆: list.html?category3Id=61&trademark=2:华为&props=4:256GB:机身存储&props=3:8GB:运行内存 前端偷懒了直接在 ${urlParam} + '&order=1:' + ${orderMap.sort == 'asc' ? 'desc': 'asc'},如果没有urlParam直接报错
        //所以要从searchParamVO中构建urlParam
        String urlParam = buildUrlParam(searchParamVO);
        respVO.setUrlParam(urlParam);

        //7、排序信息
        if(!StringUtils.isEmpty(searchParamVO.getOrder())){
            SearchRespVO.OrderMap orderMap = new SearchRespVO.OrderMap(); //1:asc 1: hot score 2: price
            String[] split = searchParamVO.getOrder().split(":");
            orderMap.setType(split[0]);
            orderMap.setSort(split[1]);
            respVO.setOrderMap(orderMap);
        }

        //8、商品列表
        List<Goods> goods = result.getSearchHits().stream()
                .map(item -> {
                    Goods content = item.getContent();
                    if(!StringUtils.isEmpty(searchParamVO.getKeyword())){ //如果有keyword，返回结果需高亮
                        //模糊检索 高亮显示
                        String newTitle = item.getHighlightField("title").get(0);
                        content.setTitle(newTitle);
                    }
                    return content;
                })
                .collect(Collectors.toList());
        respVO.setGoodsList(goods);


        //9、页码
        respVO.setPageNo(searchParamVO.getPageNo());
        //总记录
        //总页码： 总记录数%每页大小==0？总记录数/每页大小:总记录数/每页大小 + 1
        long totalHits = result.getTotalHits();
        respVO.setTotalPages(totalHits%pageSize==0?totalHits/pageSize:totalHits/pageSize+1);

        return respVO;
    }

    private String buildUrlParam(SearchParamVO searchParamVO) {
        StringBuilder sb = new StringBuilder("list.html?");
        //分类参数
        if (searchParamVO.getCategory1Id()!=null) {
            sb.append("&category1Id="+searchParamVO.getCategory1Id()); //前面多一个&符号不影响url，只不过k-v为空
        }
        if (searchParamVO.getCategory2Id()!=null) {
            sb.append("&category2Id="+searchParamVO.getCategory2Id());
        }
        if (searchParamVO.getCategory3Id()!=null) {
            sb.append("&category3Id="+searchParamVO.getCategory3Id());
        }

        //keyword
        if (!StringUtils.isEmpty(searchParamVO.getKeyword())) {
            sb.append("&keyword="+searchParamVO.getKeyword());
        }

        //品牌
        if(!StringUtils.isEmpty(searchParamVO.getTrademark())){
            sb.append("&trademark="+searchParamVO.getTrademark());
        }

        //属性
        if(searchParamVO.getProps()!=null && searchParamVO.getProps().length>0){
            Arrays.stream(searchParamVO.getProps()).forEach(item->{
                sb.append("&props="+item);
            });
        }


        //排序不要
        return sb.toString();
    }

    private static Query getQuery(SearchParamVO searchParamVO) {
        // /list.htm1?category3Id=61&trademark=2:华为&props=4:256GB:机身存储&props=3:8GB:运行内存&order=2:desc&pageNo=1&keyword=华为
        //有时候可能有category3Id，有时候可能没有，所以不能简单使用Spring Data ES自动生成的方法，需要自己用ESRestTemplate写DSL语句

        //=============查询开始==============
        //1. 查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery(); //对照着resources/dsl.json来配置条件
        //构建bool里面的查询条件
        //1.1）、一级分类
        if (searchParamVO.getCategory1Id()!=null) {
            TermQueryBuilder term = QueryBuilders.termQuery("category1Id", searchParamVO.getCategory1Id());
            boolQuery.must(term);
        }
        //1.2）、二级分类
        if (searchParamVO.getCategory2Id() != null) {
            TermQueryBuilder term = QueryBuilders.termQuery("category2Id", searchParamVO.getCategory2Id());
            boolQuery.must(term);
        }
        //1.3）、三级分类
        if (searchParamVO.getCategory3Id() != null) {
            TermQueryBuilder term = QueryBuilders.termQuery("category3Id", searchParamVO.getCategory3Id());
            boolQuery.must(term);
        }
        //1.4）、关键字查询 keyword=华为
        if (!StringUtils.isEmpty(searchParamVO.getKeyword())) {
            MatchQueryBuilder match = QueryBuilders.matchQuery("title", searchParamVO.getKeyword());
            boolQuery.must(match);
        }
        //1.5）、品牌查询 trademark=2:华为
        if (!StringUtils.isEmpty(searchParamVO.getTrademark())) {
            String[] split = searchParamVO.getTrademark().split(":");
            TermQueryBuilder term = QueryBuilders.termQuery("tmId", split[0]);
            boolQuery.must(term);
        }
        //1.6）、属性查询 props=4:256GB:机身存储&props=3:8GB:运行内存
        if (searchParamVO.getProps()!=null && searchParamVO.getProps().length>0) {
            Arrays.stream(searchParamVO.getProps())
                    .forEach(item->{
                        BoolQueryBuilder boolQuery2 = QueryBuilders.boolQuery();

                        //4:256GB:机身存储
                        String[] split = item.split(":");
                        //属性id条件
                        boolQuery2.must(QueryBuilders.termQuery("attrs.attrId",split[0]));
                        //属性值条件
                        boolQuery2.must(QueryBuilders.termQuery("attrs.attrValue",split[1]));

                        NestedQueryBuilder nested = QueryBuilders.nestedQuery("attrs",boolQuery2, ScoreMode.None);
                        boolQuery.must(nested);
                    });
        }
        //=============查询结束==============

        //2. 原生检索: 代表能使用原生的DSL进行检索
        NativeSearchQuery query = new NativeSearchQuery(boolQuery);

        //=============排序开始==============
        if (!StringUtils.isEmpty(searchParamVO.getOrder())) {
            //order=1/2:asc/desc
            String[] split = searchParamVO.getOrder().split(":");
            //1. 排序要按哪个属性 2. 按什么顺序
            Sort.Direction direction = "asc".equals(split[1])?Sort.Direction.ASC:Sort.Direction.DESC;
            Sort sort = null;
            switch (split[0]){
                case "1":
                    sort = Sort.by(direction, "hotScore");
                    break;
                case "2":
                    sort = Sort.by(direction, "price");
                    break;
                default:
                    sort = Sort.by(Sort.Direction.DESC, "hotScore");
            }
            query.addSort(sort);
        }
        //=============排序结束==============

        //=============分页开始==============
        Pageable pageable = PageRequest.of(searchParamVO.getPageNo()-1, 10); //PageRequest.of()的pageNo是从0开始的
        query.setPageable(pageable);
        //=============分页结束==============

        //=============聚合分析开始 - 品牌============
        //品牌id聚合
        TermsAggregationBuilder tmIdAgg = AggregationBuilders
                .terms("tmIdAgg")
                .field("tmId")
                .size(200);

        //品牌name子聚合
        TermsAggregationBuilder tmNameAgg = AggregationBuilders
                .terms("tmNameAgg")
                .field("tmName")
                .size(1);
        tmIdAgg.subAggregation(tmNameAgg);

        //品牌logo子聚合
        TermsAggregationBuilder tmLogoAgg = AggregationBuilders
                .terms("tmLogoAgg")
                .field("tmLogoUrl")
                .size(1);
        tmIdAgg.subAggregation(tmLogoAgg);

        query.addAggregation(tmIdAgg);


        //=============聚合分析开始 - 平台属性============
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrAgg", "attrs");

        //平台属性id聚合分析
        TermsAggregationBuilder attrIdAgg = AggregationBuilders
                .terms("attrIdAgg")
                .field("attrs.attrId")
                .size(200);

        //平台属性名聚合分析
        TermsAggregationBuilder attrNameAgg = AggregationBuilders
                .terms("attrNameAgg")
                .field("attrs.attrName")
                .size(1);
        attrIdAgg.subAggregation(attrNameAgg);

        //平台属性值聚合分析
        TermsAggregationBuilder attrValueAgg = AggregationBuilders
                .terms("attrValueAgg")
                .field("attrs.attrValue")
                .size(100);
        attrIdAgg.subAggregation(attrValueAgg);

        attrAgg.subAggregation(attrIdAgg);
        query.addAggregation(attrAgg);
        //=============聚合分析结束==============

        //==============高亮开始===============
        if(!StringUtils.isEmpty(searchParamVO.getKeyword())){ //只有有关键字检索的时候才需要高亮
            //1、构建高亮
            HighlightBuilder builder = new HighlightBuilder()
                    .field("title")
                    .preTags("<span style='color:red'>")
                    .postTags("</span>");

            HighlightQuery highlightQuery = new HighlightQuery(builder);
            query.setHighlightQuery(highlightQuery);
        }
        //==============高亮结束===============

        return query;
    }

    @Override
    public void up(Goods goods) {
        goodsRepository.save(goods);
    }

    @Override
    public void down(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    @Override
    public void updateHotScore(Long skuId, Long score) {
        ////1、查询到原来的数据
        //Goods goods = goodsRepository.findById(skuId).get();
        ////2、更新热度分
        //goods.setHotScore(score);
        ////3、保存
        //goodsRepository.save(goods); //全量更新，其他没有的字段 setXXX 会直接变成默认值

        Document document = Document.create();
        document.put("hotScore", score);

        UpdateQuery updateQuery = UpdateQuery.builder("" + skuId)
                .withDocAsUpsert(true)
                .withDocument(document)
                .build();

        elasticsearchRestTemplate.update(updateQuery, IndexCoordinates.of("goods"));

    }
}
