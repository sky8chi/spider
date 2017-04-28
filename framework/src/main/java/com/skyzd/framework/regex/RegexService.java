package com.skyzd.framework.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sky.chi on 4/15/2017 3:46 PM.
 * Email: sky8chi@gmail.com
 */
public class RegexService implements IRegexService {

    public String[] getMultiGroup(String content, String regex, int flags) {
        List<String[]> result = getMultiGroups(content, regex, flags, false);
        return result != null ? result.get(0) : null;
    }

    public List<String[]> getMultiGroups(String content, String regex, int flags) {
        return getMultiGroups(content, regex, flags, true);
    }

    public String getSingleGroup(String content, String regex, int flags) {
        List<String> result = getSingleGroups(content, regex, flags, false);
        return result != null ? result.get(0) : null;
    }

    public List<String> getSingleGroups(String content, String regex, int flags) {
        return getSingleGroups(content, regex, flags, true);
    }

    public boolean match(String content, String regex, int flags) {
        Matcher matcher = getMatcher(content, regex, flags);
        return matcher == null ? false : matcher.find();
    }

    public String[] getMultiGroup(String content, String regex) {
        List<String[]> result = getMultiGroups(content, regex, DEFAULT_FLAG,
                false);
        return result != null ? result.get(0) : null;
    }

    public List<String[]> getMultiGroups(String content, String regex) {
        return getMultiGroups(content, regex, DEFAULT_FLAG, true);
    }

    public String getSingleGroup(String content, String regex) {
        List<String> result = getSingleGroups(content, regex, DEFAULT_FLAG,
                false);
        return result != null ? result.get(0) : null;
    }

    public List<String> getSingleGroups(String content, String regex) {
        return getSingleGroups(content, regex, DEFAULT_FLAG, true);
    }

    public boolean match(String content, String regex) {
        return match(content, regex, DEFAULT_FLAG);
    }

	/* ==========================private=========================== */

    private static List<String[]> getMultiGroups(String content, String regex,
                                                 int flags, boolean isGoOn) {
        Matcher matcher = getMatcher(content, regex, flags);
        if (matcher == null) {
            return null;
        }
        List<String[]> result = new ArrayList<String[]>(1);
        while (matcher.find()) {
            int groupCount = matcher.groupCount();
            String[] strArr = new String[groupCount];
            for (int i = 0; i < groupCount; i++) {
                strArr[i] = matcher.group(i + 1);
            }
            result.add(strArr);
            if (!isGoOn) {
                break;
            }
        }
        return result.isEmpty() ? null : result;
    }

    private static List<String> getSingleGroups(String content, String regex,
                                                int flags, boolean isGoOn) {
        Matcher matcher = getMatcher(content, regex, flags);
        if (matcher == null) {
            return null;
        }
        List<String> result = new ArrayList<String>(1);
        while (matcher.find()) {
            result.add(matcher.group(1));
            if (!isGoOn) {
                break;
            }
        }
        return result.isEmpty() ? null : result;
    }

    /**
     * 获取匹配对象
     */
    private static Matcher getMatcher(String content, String regex, int flags) {
        if (regex == null || content == null) {
            return null;
        }
        Pattern pattern = Pattern.compile(regex, flags);
        Matcher matcher = pattern.matcher(content);
        return matcher;
    }

    public String replaceAll(String string, Pattern pattern, ReplaceCallback replacement) {
        return replaceAll(string, pattern, replacement, false);
    }

    private String replaceAll(String string, Pattern pattern, ReplaceCallback replacement, boolean isOnlyFirst) {
        if (string == null) {
            return null;
        }
        Matcher m = pattern.matcher(string);
        if (m.find()) {
            StringBuffer sb = new StringBuffer();
            int index = 0;
            do {
                m.appendReplacement(sb, replacement.replace(m.group(0), index++, m));
            } while (!isOnlyFirst && m.find());
            m.appendTail(sb);
            return sb.toString();
        }
        return string;
    }

    public String replaceAll(String string, String regex, ReplaceCallback replacement) {
        return replaceAll(string, Pattern.compile(regex), replacement);
    }

    public String replaceFirst(String string, String regex, ReplaceCallback replacement) {
        return replaceFirst(string, Pattern.compile(regex), replacement);
    }

    public String replaceFirst(String string, Pattern pattern, ReplaceCallback replacement) {
        return replaceAll(string, pattern, replacement, true);
    }
}
