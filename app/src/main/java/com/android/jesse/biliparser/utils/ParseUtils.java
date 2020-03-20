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

    private static final String TAG = ParseUtils.class.getSimpleName();

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
            SearchResultVideoBean videoBean = new SearchResultVideoBean();
            Element videoItem = elements.get(i);
            LogUtils.d(TAG+" videoItem html : \n"+videoItem.outerHtml());
            //parse url
            Element a = videoItem.child(0);
            String url = a.attr("href");
            videoBean.setUrl(url);
            //parse cover
            String cover = a.select("img[src]").first().attr("src");
            videoBean.setCover(cover);
            //parse title
            String title = a.attr("title");
            videoBean.setTitle(title);
            //parse playCount
            Element div_tags = videoItem.selectFirst("div.tags");
            Element span1 = div_tags.child(0);
            String playCount = span1.text();
            videoBean.setPlayCount(playCount);
            //parse date
            Element span2 = div_tags.child(2);
            String date = span2.text();
            videoBean.setDate(date);
            //parse author
            Element span3 = div_tags.child(3);
            String author = span3.text();
            videoBean.setAuthor(author);
            LogUtils.d(TAG+" videoBean : \n"+videoBean);
        }
        return videoBeanList;
    }

}
