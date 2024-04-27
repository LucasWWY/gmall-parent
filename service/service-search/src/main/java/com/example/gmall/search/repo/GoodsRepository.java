package com.example.gmall.search.repo;

import com.example.gmall.search.Goods;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 15/4/2024 - 6:58 am
 * @Description
 */
@Repository
public interface GoodsRepository extends PagingAndSortingRepository<Goods, Long> {
}
