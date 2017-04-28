package com.skyzd.framework.regex;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by sky.chi on 4/15/2017 3:45 PM.
 * Email: sky8chi@gmail.com
 */
public interface IRegexService {
    public static final int DEFAULT_FLAG = 0;

    /**
     *	获取不忽略大小写 单个单分组
     */
    String getSingleGroup(String content, String regex);

    /**
     * flags - default:0, special: Pattern.CASE_INSENSITIVE, MULTILINE, DOTALL, UNICODE_CASE, CANON_EQ, UNIX_LINES, LITERAL and COMMENTS
     */
    String getSingleGroup(String content, String regex, int flags);


    /**
     *	获取不忽略大小写 所有单分组
     */
    List<String> getSingleGroups(String content, String regex);
    /**
     * flags - default:0, special: Pattern.CASE_INSENSITIVE, MULTILINE, DOTALL, UNICODE_CASE, CANON_EQ, UNIX_LINES, LITERAL and COMMENTS
     */
    List<String> getSingleGroups(String content, String regex, int flags);


    /**
     *	获取不忽略大小写 单个多分组
     */
    String[] getMultiGroup(String content, String regex);
    /**
     * flags - default:0, special: Pattern.CASE_INSENSITIVE, MULTILINE, DOTALL, UNICODE_CASE, CANON_EQ, UNIX_LINES, LITERAL and COMMENTS
     */
    String[] getMultiGroup(String content, String regex, int flags);


    /**
     *	获取不忽略大小写 所有多分组
     */
    List<String[]> getMultiGroups(String content, String regex);
    /**
     * flags - default:0, special: Pattern.CASE_INSENSITIVE, MULTILINE, DOTALL, UNICODE_CASE, CANON_EQ, UNIX_LINES, LITERAL and COMMENTS
     */
    List<String[]> getMultiGroups(String content, String regex, int flags);


    /**
     *	是否不忽略大小写匹配
     */
    boolean match(String content, String regex);
    /**
     * flags - default:0, special: Pattern.CASE_INSENSITIVE, MULTILINE, DOTALL, UNICODE_CASE, CANON_EQ, UNIX_LINES, LITERAL and COMMENTS
     */
    boolean match(String content, String regex, int flags);

    String replaceAll(String string, Pattern pattern, ReplaceCallback replacement) ;

    String replaceAll(String string, String regex, ReplaceCallback replacement);

    String replaceFirst(String string, String regex, ReplaceCallback replacement);

    String replaceFirst(String string, Pattern pattern, ReplaceCallback replacement);
}
