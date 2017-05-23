package com.ald.fanbei.api.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * 
 *@类CollectionConverterUtil.java 的实现描述：集合类转换器
 *@author 陈金虎 2017年1月16日 下午11:33:25
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class CollectionConverterUtil {

    /**
     * 由list<S>转换成map<T,S>
     * @param list
     * @param converter
     * @return
     */
    public static <S, T> Map<T, S> convertToMapFromList(List<S> list, Converter<S, T> converter) {
        if (list == null || list.size() < 1) {
            return Collections.emptyMap();
        }
        Map<T, S> maps = new HashMap<T, S>(list.size());
        for (S value : list) {
            if (value == null) {
                continue;
            }
            T target = converter.convert(value);
            maps.put(target, value);
        }
        return maps;
    }
    
    /**
     * 由List<S>转换成map<T,List<S>>
     * @param list
     * @param converter
     * @return
     */
    public static <S, T> Map<T, List<S>> convertToMapListFromList(List<S> list, Converter<S, T> converter) {
        if (list == null || list.size() < 1) {
            return Collections.emptyMap();
        }
        Map<T, List<S>> maps = new HashMap<T, List<S>>();
        for (S value : list) {
            if (value == null) {
                continue;
            }
            T target = converter.convert(value);
            if(maps.get(target) == null){
                List<S> tempList = new ArrayList<S>();
                tempList.add(value);
                maps.put(target, tempList);
            }else{
                List<S> tempList = maps.get(target);
                tempList.add(value);
            }
        }
        return maps;
    }

    /**
     * 由list<S>转换成list<T>
     * @param sourceList
     * @param converter
     * @return
     */
    public static <S, T> List<T> convertToListFromList(List<S> sourceList, Converter<S, T> converter) {
        if (sourceList == null || sourceList.size() < 1) {
            return Collections.emptyList();
        }
        List<T> targetList = new ArrayList<T>(sourceList.size());
        for (S source : sourceList) {
            if (source == null) {
                continue;
            }
            T target = converter.convert(source);
            if (target != null) {
                targetList.add(target);
            }
        }
        return targetList;
    }

    /**
     * 由List<S>转换成Set<T>
     * @param sourceList 原转换对象
     * @param converter 转换器
     * @return
     */
    public static <S, T> Set<T> convertToSetFromList(List<S> sourceList, Converter<S, T> converter) {
        if (sourceList == null || sourceList.size() < 1) {
            return Collections.emptySet();
        }
        Set<T> targetSet = new HashSet<T>(sourceList.size());
        for (S source : sourceList) {
            if (source == null) {
                continue;
            }
            T target = converter.convert(source);
            if (target != null) {
                targetSet.add(target);
            }
        }
        return targetSet;
    }

    /**
     * 由对象S转换成对象T
     * @param source 原对象
     * @param converter
     * @return
     */
    public static <S, T> T convertToObjectFromObject(S source, Converter<S, T> converter) {
        if (source == null) {
            return null;
        }
        T target = converter.convert(source);
        return target;
    }

    /**
     * 由list转换成Map，map的key由converter获取,map的value由converterValue获取
     * @param list
     * @param converterKey
     * @param converterValue
     * @return
     */
    public static <S, T, M> Map<T, M> convertToMapFromList(List<S> list, Converter<S, T> converterKey,
                                                         Converter<S, M> converterValue) {
        if (list == null || list.size() < 1) {
            return Collections.emptyMap();
        }
        Map<T, M> maps = new HashMap<T, M>(list.size());
        for (S value : list) {
            if (value == null) {
                continue;
            }
            T targetKey = converterKey.convert(value);
            M targetValue = converterValue.convert(value);
            maps.put(targetKey, targetValue);
        }
        return maps;
    }

    /**
     * 在List<T>中查询目标结果集
     * 
     * @param sourceList 原对象列表
     * @param searchValue 需要搜索的关键字
     * @param searcher 搜索器
     * @return
     */
    public static <S, T, D> List<T> searchResultList(List<S> sourceList, D searchValue, ObjectSearcher<S, T, D> searcher) {
        if (sourceList == null || sourceList.size() < 1) {
            return Collections.emptyList();
        }
        List<T> resultList = new ArrayList<T>(sourceList.size());
        for (S source : sourceList) {
            if (source == null) {
                continue;
            }
            T result = searcher.search(source, searchValue);
            if (result != null) {
                resultList.add(result);
            }
        }
        return resultList;
    }

    /**
     * 由Map<S,T>转换成List<T>
     * @param map
     * @return
     */
    public static <S, T> List<T> convertToListFromMap(Map<S, T> map) {
        if (map == null || map.size() < 1) {
            return Collections.emptyList();
        }
        List<T> resultList = new ArrayList<T>(map.size());
        for (Map.Entry<S, T> source : map.entrySet()) {
            if (null == source || null == source.getValue()) continue;
            resultList.add(source.getValue());
        }
        return resultList;
    }

    /**
     * 由Map<S,T>转换成List<K>
     * @param map
     * @param converter
     * @return
     */
    public static <S, T, K> List<K> convertToListFromMap(Map<S, T> map, Converter<T, K> converter) {
        if (null == map || map.size() < 1) {
            return Collections.emptyList();
        }
        List<K> resultList = new ArrayList<K>(map.size());
        for (Map.Entry<S, T> source : map.entrySet()) {
            if (null == source || null == source.getValue()) continue;
            K result = converter.convert(source.getValue());
            if (null != result) {
                resultList.add(result);
            }
        }
        return resultList;
    }

    /**
     * 由Array<S>转换成List<T>
     * @param sourceArray
     * @param converter
     * @return
     */
    public static <S, T> List<T> convertToListFromArray(S[] sourceArray, Converter<S, T> converter) {
        if (sourceArray == null || sourceArray.length < 1) {
            return Collections.emptyList();
        }
        List<T> targetList = new ArrayList<T>(sourceArray.length);
        for (S source : sourceArray) {
            if (null == source) {
                continue;
            }
            T target = converter.convert(source);
            if (target != null) {
                targetList.add(target);
            }
        }
        return targetList;
    }
    
    /**
     * 高效removeAll的方法，方法改进理由如下：
     * 1.removeAll执行效率比remove低
     * 2.LinkedList插入更新效率高，ArrayList查询效率高
     * @param bigList 原集合
     * @param smallList 要被去除的集合
     * @return 返回removeAll后的List
     */
    public static <T> List<T> removeAll(List<T> bigList,List<T> smallList){
    	LinkedList<T> sourceList = new LinkedList<T>(bigList);//大集合用LinkedList
    	HashSet<T> targetList = new HashSet<T>(smallList);//小集合用HashSet
    	Iterator<T> iter = sourceList.iterator();
    	while(iter.hasNext()){  
    		if(targetList.contains(iter.next())){  
    			iter.remove();  
    		}
    	}
    	return sourceList;  
    }
}
