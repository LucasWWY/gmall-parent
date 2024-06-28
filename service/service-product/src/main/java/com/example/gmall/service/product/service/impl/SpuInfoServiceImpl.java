package com.example.gmall.service.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.gmall.model.product.entity.SpuImage;
import com.example.gmall.model.product.entity.SpuInfo;
import com.example.gmall.model.product.entity.SpuSaleAttr;
import com.example.gmall.model.product.entity.SpuSaleAttrValue;
import com.example.gmall.service.product.mapper.SpuInfoMapper;
import com.example.gmall.service.product.service.SpuImageService;
import com.example.gmall.service.product.service.SpuInfoService;
import com.example.gmall.service.product.service.SpuSaleAttrService;
import com.example.gmall.service.product.service.SpuSaleAttrValueService;
import com.example.gmall.model.product.vo.SpuSaveInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author wangweiyedemacbook
* @description 针对表【spu_info(商品表)】的数据库操作Service实现
* @createDate 2024-02-06 23:58:53
*/
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
    implements SpuInfoService{

    @Autowired
    SpuImageService spuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SpuSaleAttrValueService spuSaleAttrValueService;

    //这么写太麻烦，可以使用Java 8 Stream API新特性
    //@Override
    //public void saveSpuInfoData(SpuSaveInfoVO spuSaveInfoVO) {
    //    //1. 存spu_info
    //    SpuInfo spuInfo = new SpuInfo();
    //    //老办法：使用插件 在;前alt + enter自动生成setter
    //    //spuInfo.setSpuName(spuSaveInfoVO.getSpuName());
    //    //spuInfo.setDescription(spuSaveInfoVO.getDescription());
    //    //spuInfo.setCategory3Id(spuSaveInfoVO.getCategory3Id());
    //    //spuInfo.setTmId(spuSaveInfoVO.getTmId());
    //
    //    BeanUtils.copyProperties(spuSaveInfoVO, spuInfo);
    //    this.save(spuInfo);
    //
    //    //2. 存spu_image
    //    List<SpuSaveInfoVO.SpuImageListDTO> spuImageList = spuSaveInfoVO.getSpuImageList();
    //    List<SpuImage> spuImages = new ArrayList<>();
    //    spuImageList.forEach(spuImageListDTO -> {
    //        SpuImage spuImage = new SpuImage();
    //        BeanUtils.copyProperties(spuImageListDTO, spuImage);
    //        spuImage.setSpuId(spuInfo.getId()); //回填spu_id
    //        spuImages.add(spuImage);
    //    });
    //    spuImageService.saveBatch(spuImages);
    //
    //    //3. 存spu_sale_attr
    //    List<SpuSaveInfoVO.SpuSaleAttrListDTO> spuSaleAttrList = spuSaveInfoVO.getSpuSaleAttrList();
    //    List<SpuSaleAttr> spuSaleAttrs = new ArrayList<>();
    //    spuSaleAttrList.forEach(spuSaleAttrListDTO -> {
    //        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
    //        BeanUtils.copyProperties(spuSaleAttrListDTO, spuSaleAttr);
    //        spuSaleAttr.setSpuId(spuInfo.getId()); //回填spu_id
    //        spuSaleAttrs.add(spuSaleAttr);
    //
    //        //4. 存spu_sale_attr_value
    //        List<SpuSaveInfoVO.SpuSaleAttrListDTO.SpuSaleAttrValueListDTO> spuSaleAttrValueList = spuSaleAttrListDTO.getSpuSaleAttrValueList();
    //        spuSaleAttrValueList.forEach(spuSaleAttrValueDTO -> {
    //            SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
    //            BeanUtils.copyProperties(spuSaleAttrValueDTO, spuSaleAttrValue);
    //            spuSaleAttrValue.setSpuId(spuInfo.getId()); //回填spu_id
    //            spuSaleAttrValue.setBaseSaleAttrId(spuSaleAttr.getBaseSaleAttrId()); //回填base_sale_attr_id
    //            spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName()); //回填sale_attr_name
    //            spuSaleAttrValueService.save(spuSaleAttrValue);
    //        });
    //    });
    //}

    //Java 8 Stream API新特性
    //public static void main(String[] args) {
    //    System.out.println("主线程：" + Thread.currentThread());
    //    List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
    //    //1、告诉JVM把每个元素 * 2，怎么安排自己决定（流的自动迭代），不需要和for一样手动迭代，这种就是声明式编程，以前是 命令式编程
    //    List<Integer> integer = list
    //            .stream()
    //            .parallel() //并发，测试阻塞后是否会开启新线程：会
    //            .map(item -> {
    //                System.out.println(Thread.currentThread() + "正在处理：[" + item + "]");
    //                if (item == 2) {
    //                    try {
    //                        Thread.sleep(2000);
    //                    } catch (InterruptedException e) {
    //                        e.printStackTrace();
    //                    }
    //                }
    //                return item * 2;
    //            })
    //            .collect(Collectors.toList());
    //
    //    list.stream()
    //            .flatMap(item -> Arrays.asList(item + 6, item + 8).stream()) //一对多，每个元素变成两个新元素
    //            .collect(Collectors.toList());
    //}

    @Override
    public void saveSpuInfoData(SpuSaveInfoVO spuSaveInfoVO) {
        //1. 存spu_info
        SpuInfo spuInfo = new SpuInfo();

        BeanUtils.copyProperties(spuSaveInfoVO, spuInfo);
        this.save(spuInfo);


        //2. 存spu_image
        //List<SpuSaveInfoVO.SpuImageListDTO> spuImageList = spuSaveInfoVO.getSpuImageList();
        //List<SpuImage> spuImages = new ArrayList<>();
        //spuImageList.forEach(spuImageListDTO -> {
        //    SpuImage spuImage = new SpuImage();
        //    BeanUtils.copyProperties(spuImageListDTO, spuImage);
        //    spuImage.setSpuId(spuInfo.getId()); //回填spu_id
        //    spuImages.add(spuImage);
        //});

        List<SpuImage> spuImages = spuSaveInfoVO.getSpuImageList()
                .stream()
                .map(item -> {
                    SpuImage spuImage = new SpuImage();
                    BeanUtils.copyProperties(item, spuImage);
                    spuImage.setSpuId(spuInfo.getId()); //回填spu_id
                    return spuImage;
                }).collect(Collectors.toList());
        spuImageService.saveBatch(spuImages);

        //3. 存spu_sale_attr
        List<SpuSaleAttr> spuSaleAttrs = spuSaveInfoVO.getSpuSaleAttrList()
                .stream()
                .map(item -> {
                    SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
                    BeanUtils.copyProperties(item, spuSaleAttr);
                    spuSaleAttr.setSpuId(spuInfo.getId()); //回填spu_id
                    return spuSaleAttr;
                }).collect(Collectors.toList());
        spuSaleAttrService.saveBatch(spuSaleAttrs);

        //4. 存spu_sale_attr_value
        List<SpuSaleAttrValue> spuSaleAttrValues = spuSaveInfoVO.getSpuSaleAttrList()
                .stream()
                .flatMap(item -> { //一对多，每个销售属性 对应多个 销售属性值
                    Stream<SpuSaleAttrValue> spuSaleAttrValueStream = item.getSpuSaleAttrValueList()
                            .stream()
                            .map(val -> {
                                SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
                                BeanUtils.copyProperties(item, spuSaleAttrValue);
                                spuSaleAttrValue.setSpuId(spuInfo.getId()); //回填spu_id
                                spuSaleAttrValue.setSaleAttrName(item.getSaleAttrName());
                                return spuSaleAttrValue;
                            });
                    return spuSaleAttrValueStream;
                }).collect(Collectors.toList());
        //在这段代码中，通过使用flatMap操作符将每个销售属性（SpuSaleAttr对象）对应的销售属性值列表item.getSpuSaleAttrValueList()（List<SpuSaleAttrValueListDTO>）
        //转换为一个流（Stream），然后使用map操作符将每个SpuSaleAttrValueListDTO对象转换为SpuSaleAttrValue对象，并将同一销售属性的所有SpuSaleAttrValue对象转化成Stream<SpuSaleAttrValue>
        //flatMap会把所有单个Stream<SpuSaleAttrValue>合并成一个单一的流，最终得到的流是Stream<SpuSaleAttrValue>类型的流
        //这个流是通过将原始的Stream<SpuSaleAttrListDTO>转换而来的，但它不再是原始的流了。
        //最后，使用collect操作符将流中的所有SpuSaleAttrValue对象收集到一个List集合中
        spuSaleAttrValueService.saveBatch(spuSaleAttrValues);
    }
}




