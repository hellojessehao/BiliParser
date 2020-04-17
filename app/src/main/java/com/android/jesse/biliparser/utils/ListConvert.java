package com.android.jesse.biliparser.utils;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * @Description: ROOM List类型转换器
 * @author: zhangshihao
 * @date: 2020/4/17
 */
public class ListConvert {

    @TypeConverter
    public List<String> stringToObject(String string){
        return new Gson().fromJson(string, new TypeToken<List<String>>() {
        }.getType());
    }

    @TypeConverter
    public String objectToString(List<String> list){
        return new Gson().toJson(list);
    }

}
