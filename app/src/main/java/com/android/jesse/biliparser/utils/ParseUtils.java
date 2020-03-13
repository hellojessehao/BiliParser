package com.android.jesse.biliparser.utils;

import com.android.jesse.biliparser.network.model.bean.SearchResultVideoBean;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: html解析工具类
 * @author: zhangshihao
 * @date: 2020/3/13
 */
public class ParseUtils {

    /**
     * 解析搜索列表
     * @param elements 视频元素列表
     * @return 搜索结果视频数据列表
     */
    public static List<SearchResultVideoBean> parseSearchResultHtml(Elements elements){
        if(elements == null || elements.size() == 0){
            return new ArrayList<>();
        }
        List<SearchResultVideoBean> videoBeanList = new ArrayList<>();
        for(int i=0;i<elements.size();i++){
            Element videoItem = elements.get(i);
        }
        return videoBeanList;
    }

}
