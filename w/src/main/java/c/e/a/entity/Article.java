package c.e.a.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Date: 13-10-14 - 下午10:42
 */
public class Article implements Serializable {
    private int id;
    private String title;
    private String content; // using template language
    private Date date; //no record of modified time

}