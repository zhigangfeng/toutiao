package com.newcoder.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/9.
 */
public class NewsScoreUtil {
    public static double newsScore(int likeCount, int dislikeCount, Date date,int commentCount){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int ts=0;
        try {
            ts=(int)(date.getTime()-df.parse("2017-01-01 00:00:00").getTime())/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int x=likeCount-dislikeCount;
        int y;
        if(x>0){
            y=1;
        }else if(x==0){
            y=0;
        }else{
            y=-1;
        }
        int z=x>0? x: 1;
        return Math.log10(z+commentCount*10)+(y*ts)/45000;
    }
}
