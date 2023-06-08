package com.abc.luntan.utils;


import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import toolgood.words.IllegalWordsSearch;
import toolgood.words.IllegalWordsSearchResult;
import toolgood.words.StringSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.abc.luntan.utils.RedisConstants.SYSTEM_BADWORD;

@Slf4j
public class BadWordUtil {

    private StringSearch search ;

    private static volatile BadWordUtil badWordUtil=null;

    private BadWordUtil() {
        // Redis 获取所有的key
        search = new StringSearch();
    }


    public static BadWordUtil getInstance() {
        if (badWordUtil == null) {
            synchronized (BadWordUtil.class){
                if (badWordUtil == null) {
                    badWordUtil = new BadWordUtil();
                }
            }
        }
        return badWordUtil;
    }


    public boolean hasBadWord(String text) {
        return search.ContainsAny(text);
    }


    public String replaceBadWord(String text){
        return search.Replace(text,'*');
    }

    public List<String> getBadWords(String text) {
        return search.FindAll(text);
    }

    public void setKeywords(List<String> list){
        search.SetKeywords(list);
    }
}
