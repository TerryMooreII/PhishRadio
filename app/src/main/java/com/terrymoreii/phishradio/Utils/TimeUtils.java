package com.terrymoreii.phishradio.Utils;

import java.util.Date;

/**
 * Created by tmoore on 7/23/14.
 */
public class TimeUtils {

    public static String getTime(int milli){
        Date date = new Date();
        date.setTime(milli);
        int sec = date.getSeconds();
        return date.getMinutes() + ":" + (sec < 10 ? "0" + sec : sec) ;

    }
}
