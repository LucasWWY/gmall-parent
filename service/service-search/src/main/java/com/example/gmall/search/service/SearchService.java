package com.example.gmall.search.service;

import com.example.gmall.model.search.Goods;
import com.example.gmall.model.search.vo.SearchParamVO;
import com.example.gmall.model.search.vo.SearchRespVO;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 6:26 am
 * @Description
 */
public interface SearchService {

    SearchRespVO search(SearchParamVO searchParamVO);

    void up(Goods goods);

    void down(Long skuId);

    void updateHotScore(Long skuId, Long score);
}
