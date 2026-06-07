package com.bsyun.xuanxueapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 奇门遁甲核心算法工具类
 */
public class CommmonUtil {
    private static final String TAG = "CommmonUtil";

    public static final String TIME = "time";

    // 十天干
    public static String[] tiangan = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    // 十二地支
    public static String[] dizhi = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    // 八门
    public static String[] yuanBamen = {"天蓬", "天芮", "天冲", "天辅", "天英", "天芮", "天柱", "天心", "天禽"};
    // 九星
    public static String[] yuanJiuXing = {"天蓬", "天任", "天冲", "天辅", "天英", "天芮", "天柱", "天心", "天禽"};
    // 八门名称
    public static String[] bamenNames = {"休门", "死门", "伤门", "杜门", "杜门", "景门", "死门", "惊门", "开门"};
    // 八神
    public static String[] bashenNames = {"值符", "螣蛇", "太阴", "六合", "白虎", "玄武", "九地", "九天"};
    // 宫位图标资源ID（占位用数字，实际在Adapter中映射）
    public static int[] iconGongwei = {
            android.R.drawable.star_big_on,  // 巽4
            android.R.drawable.star_big_on,  // 离9
            android.R.drawable.star_big_on,  // 坤2
            android.R.drawable.star_big_on,  // 震3
            android.R.drawable.star_big_on,  // 中5
            android.R.drawable.star_big_on,  // 兑7
            android.R.drawable.star_big_on,  // 艮8
            android.R.drawable.star_big_on,  // 坎1
            android.R.drawable.star_big_on   // 乾6
    };

    // 九宫八卦对应的数字
    // 后天八卦数：坎1、坤2、震3、巽4、中5、乾6、兑7、艮8、离9
    // 排盘顺序：巽4 离9 坤2 / 震3 中5 兑7 / 艮8 坎1 乾6

    // 各宫位对应的地支（用于长生十二宫计算）
    // 顺序：巽4 离9 坤2 震3 中5 兑7 艮8 坎1 乾6
    public static String[] gongweiDizhi = {"辰巳", "午", "未申", "卯", "", "酉", "丑寅", "子", "亥戌"};

    // 各宫位五行：巽木 离火 坤土 震木 中土 兑金 艮土 坎水 乾金
    public static String[] gongweiWuxing = {"木", "火", "土", "木", "土", "金", "土", "水", "金"};

    // 各宫位对应月份（用于判断季节生克）
    // 顺序同上
    public static int[] gongweiMonth = {4, 6, 7, 3, 5, 9, 1, 12, 10};

    /**
     * 获取排盘信息
     *
     * @param timeStamp 时间戳
     * @param rigz      日干支
     * @return List: [isYangdun, jushu, ...]
     */
    public static List<Object> getPanInfo(long timeStamp, String rigz) {
        List<Object> info = new ArrayList<>();
        int year = DateUtil.getYearByTimeStamp(timeStamp);
        int month = DateUtil.getMonthByTimeStamp(timeStamp);
        int day = DateUtil.getDayByTimeStamp(timeStamp);

        // 根据节气判断用局
        // 阳遁：冬至到夏至 阴遁：夏至到冬至
        boolean isYangdun = isYangDun(month, day);
        int jushu = getJuShu(year, month, day, isYangdun);

        info.add(isYangdun);
        info.add(year);
        info.add(month);
        info.add(jushu);
        info.add(day);

        Log.e(TAG, "排盘信息: " + (isYangdun ? "阳遁" : "阴遁") + jushu + "局");
        return info;
    }

    /**
     * 判断是阳遁还是阴遁
     * 冬至~夏至：阳遁；夏至~冬至：阴遁
     */
    private static boolean isYangDun(int month, int day) {
        // 简化：以节气为界
        // 冬至(12月22日左右)~夏至(6月22日左右)：阳遁
        // 夏至(6月22日左右)~冬至(12月22日左右)：阴遁
        if (month < 6 || (month == 6 && day < 22)) {
            // 1月~6月21日 冬至之后夏至之前 → 阳遁
            return true;
        } else if (month > 6 || (month == 6 && day >= 22)) {
            // 6月22日~12月 → 阴遁
            if (month == 12 && day >= 22) {
                // 12月22日之后冬至 → 阳遁
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * 获取用局数（简化版）
     * 根据节气获取对应的局数
     */
    private static int getJuShu(int year, int month, int day, boolean isYang) {
        int dayOfYear = getDayOfYear(year, month, day);

        // 二十四节气对应的阳遁局
        // 冬至:阳遁一七四 小寒:阳遁二八五 大寒:阳遁三九六
        // 立春:阳遁八五二 雨水:阳遁九六三 惊蛰:阳遁一七四
        // 春分:阳遁三九六 清明:阳遁四一七 谷雨:阳遁五二八
        // 立夏:阳遁四一七 小满:阳遁五二八 芒种:阳遁六三九
        int[][] yangJu = {
                {1, 7, 4},  // 冬至
                {2, 8, 5},  // 小寒
                {3, 9, 6},  // 大寒
                {8, 5, 2},  // 立春
                {9, 6, 3},  // 雨水
                {1, 7, 4},  // 惊蛰
                {3, 9, 6},  // 春分
                {4, 1, 7},  // 清明
                {5, 2, 8},  // 谷雨
                {4, 1, 7},  // 立夏
                {5, 2, 8},  // 小满
                {6, 3, 9}   // 芒种
        };

        // 阴遁局
        int[][] yinJu = {
                {9, 3, 6},  // 夏至
                {8, 2, 5},  // 小暑
                {7, 1, 4},  // 大暑
                {2, 5, 8},  // 立秋
                {1, 4, 7},  // 处暑
                {9, 3, 6},  // 白露
                {7, 1, 4},  // 秋分
                {8, 2, 5},  // 寒露
                {9, 3, 6},  // 霜降
                {6, 9, 3},  // 立冬
                {5, 8, 2},  // 小雪
                {4, 7, 1}   // 大雪
        };

        // 获取当前节气索引（简化）
        int jieqiIndex = getJieQiIndex(dayOfYear);
        if (jieqiIndex < 0) jieqiIndex = 0;
        if (jieqiIndex > 23) jieqiIndex = 23;

        if (isYang) {
            // 阳遁：冬至~夏至 节气索引 0~11
            if (jieqiIndex <= 11) {
                int[] ju = yangJu[jieqiIndex];
                return ju[(day - 1) % 3];
            }
            return 1;
        } else {
            // 阴遁：夏至~冬至 节气索引12~23
            if (jieqiIndex >= 12 && jieqiIndex <= 23) {
                int[] ju = yinJu[jieqiIndex - 12];
                return ju[(day - 1) % 3];
            }
            return 1;
        }
    }

    /**
     * 获取一年中的第几天
     */
    private static int getDayOfYear(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取节气索引（0=冬至, 1=小寒, ..., 23=大雪）
     * 简化版：按月份估算
     */
    private static int getJieQiIndex(int dayOfYear) {
        // 粗略按日期范围映射
        if (dayOfYear < 15) return 22;      // 小寒附近
        if (dayOfYear < 45) return 23;      // 大寒附近
        if (dayOfYear < 75) return 0;       // 立春附近
        if (dayOfYear < 106) return 1;      // 雨水附近
        if (dayOfYear < 136) return 2;      // 惊蛰附近
        if (dayOfYear < 167) return 3;      // 春分附近
        if (dayOfYear < 197) return 4;      // 清明附近
        if (dayOfYear < 228) return 5;      // 谷雨附近
        if (dayOfYear < 258) return 6;      // 立夏附近
        if (dayOfYear < 289) return 7;      // 小满附近
        if (dayOfYear < 319) return 8;      // 芒种附近
        if (dayOfYear < 350) return 9;      // 夏至附近
        if (dayOfYear < 380) return 10;     // 小暑附近
        return 11;
    }

    /**
     * 获取阴阳天干映射表（地盘天干）
     */
    public static Map<Integer, String> getYinYangTianganMap(boolean isYangdun, int jushu) {
        Map<Integer, String> map = new HashMap<>();
        int[] gongwei = {4, 9, 2, 3, 5, 7, 8, 1, 6};

        // 阳遁顺排：戊己庚辛壬癸丁丙乙
        // 阴遁逆排：戊己庚辛壬癸丁丙乙（但宫位顺序相反）
        String[] paiGan = {"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};

        // 从局数对应的宫位开始排戊
        int startIndex = -1;
        for (int i = 0; i < gongwei.length; i++) {
            if (gongwei[i] == jushu) {
                startIndex = i;
                break;
            }
        }
        if (startIndex < 0) startIndex = 0;

        if (isYangdun) {
            // 阳遁顺排
            for (int i = 0; i < 9; i++) {
                int idx = (startIndex + i) % 9;
                map.put(gongwei[idx], paiGan[i]);
                Log.e(TAG, "阳遁: 宫位" + gongwei[idx] + " -> " + paiGan[i]);
            }
        } else {
            // 阴遁逆排
            for (int i = 0; i < 9; i++) {
                int idx = (startIndex - i + 9) % 9;
                map.put(gongwei[idx], paiGan[i]);
                Log.e(TAG, "阴遁: 宫位" + gongwei[idx] + " -> " + paiGan[i]);
            }
        }

        return map;
    }

    /**
     * 获取旬首干
     */
    public static String getXunshouGan(String shichen) {
        // 时柱的地支
        String dz = shichen.substring(1);
        int dzIndex = -1;
        for (int i = 0; i < dizhi.length; i++) {
            if (dizhi[i].equals(dz)) {
                dzIndex = i;
                break;
            }
        }
        if (dzIndex < 0) return "甲";

        // 旬首：甲子、甲戌、甲申、甲午、甲辰、甲寅
        // 子(0)→甲子, 丑(1)→甲子, 寅(2)→甲子, 卯(3)→甲子
        // 辰(4)→甲戌, 巳(5)→甲戌, 午(6)→甲戌, 未(7)→甲戌
        // 申(8)→甲申, 酉(9)→甲申, 戌(10)→甲申, 亥(11)→甲申
        // 实际旬首按地支每10个一组
        String[] xunshou = {"甲子", "甲戌", "甲申", "甲午", "甲辰", "甲寅"};
        int xunIndex = dzIndex / 10;
        if (xunIndex >= xunshou.length) xunIndex = xunshou.length - 1;
        return xunshou[xunIndex].substring(1); // 返回地支部分
    }

    /**
     * 获取特殊时干（时干的天干部分）
     */
    public static String getSpecialShigan(String shichen) {
        if (shichen == null || shichen.length() < 1) return "甲";
        return shichen.substring(0, 1);
    }

    /**
     * 获取默认天干（去除非标准字符）
     */
    public static String getDefaultGan(String gan) {
        if (gan == null || gan.isEmpty()) return "—";
        return gan;
    }

    /**
     * 获取旬空
     */
    public static String getXunkong(String shichen) {
        if (shichen == null || shichen.length() < 2) return "0";
        String dz = shichen.substring(1);
        int dzIndex = -1;
        for (int i = 0; i < dizhi.length; i++) {
            if (dizhi[i].equals(dz)) {
                dzIndex = i;
                break;
            }
        }
        if (dzIndex < 0) return "0";

        // 甲子旬→戌亥空(10,11) 甲戌旬→申酉空(8,9)
        // 甲申旬→午未空(6,7) 甲午旬→辰巳空(4,5)
        // 甲辰旬→寅卯空(2,3) 甲寅旬→子丑空(0,1)
        int xunIndex = dzIndex / 2;
        if (xunIndex > 5) xunIndex = 5;

        // 返回空亡地支对应的宫位索引（九宫格索引）
        int[] kongGong = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        // 简化：戌亥→乾6宫(8) 申酉→兑7宫(5) 午未→离9宫(1)
        // 辰巳→巽4宫(0) 寅卯→艮8宫(6) 子丑→坎1宫(7)
        String[][] kongMap = {
                {"戌", "亥"},  // 甲子旬
                {"申", "酉"},  // 甲戌旬
                {"午", "未"},  // 甲申旬
                {"辰", "巳"},  // 甲午旬
                {"寅", "卯"},  // 甲辰旬
                {"子", "丑"}   // 甲寅旬
        };

        if (xunIndex >= kongMap.length) xunIndex = 0;
        String k1 = kongMap[xunIndex][0];
        String k2 = kongMap[xunIndex][1];

        // 将地支映射到宫位索引（0-8）
        return getDizhiGongwei(k1) + "" + getDizhiGongwei(k2);
    }

    /**
     * 获取驿马
     */
    public static int getStarMa(String shichen) {
        if (shichen == null || shichen.length() < 2) return 0;
        String dz = shichen.substring(1);
        int dzIndex = -1;
        for (int i = 0; i < dizhi.length; i++) {
            if (dizhi[i].equals(dz)) {
                dzIndex = i;
                break;
            }
        }
        if (dzIndex < 0) return 0;

        // 寅午戌→申(8) 巳酉丑→亥(11) 申子辰→寅(2) 亥卯未→巳(5)
        int maIndex = -1;
        if (dzIndex == 2 || dzIndex == 6 || dzIndex == 10) maIndex = 8;  // 申
        else if (dzIndex == 5 || dzIndex == 9 || dzIndex == 1) maIndex = 11; // 亥
        else if (dzIndex == 8 || dzIndex == 0 || dzIndex == 4) maIndex = 2;  // 寅
        else if (dzIndex == 11 || dzIndex == 3 || dzIndex == 7) maIndex = 5; // 巳

        return getDizhiGongwei(dizhi[maIndex]);
    }

    /**
     * 根据地支获取九宫格索引
     */
    private static int getDizhiGongwei(String dz) {
        // 地支→九宫格索引映射
        // 子→坎1宫(7) 丑→艮8宫(6) 寅→艮8宫(6)
        // 卯→震3宫(3) 辰→巽4宫(0) 巳→巽4宫(0)
        // 午→离9宫(1) 未→坤2宫(2) 申→坤2宫(2)
        // 酉→兑7宫(5) 戌→乾6宫(8) 亥→乾6宫(8)
        String[] dzList = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        int[] gwIndex = {7, 6, 6, 3, 0, 0, 1, 2, 2, 5, 8, 8};

        for (int i = 0; i < dzList.length; i++) {
            if (dzList[i].equals(dz)) {
                return gwIndex[i];
            }
        }
        return 0;
    }

    /**
     * 根据RecyclerView位置获取月份（用于判断季节生克）
     */
    public static String getMonthByRecyclerPosition(int position) {
        if (position < 0 || position >= gongweiMonth.length) return "土";
        int m = gongweiMonth[position];

        // 春木 夏火 秋金 冬水 季末土
        if (m >= 2 && m <= 4) return "木";
        else if (m >= 5 && m <= 7) return "火";
        else if (m >= 8 && m <= 10) return "金";
        else if (m == 11 || m == 12 || m == 1) return "水";
        else return "土";
    }

    /**
     * 根据RecyclerView位置获取地支（用于长生十二宫）
     */
    public static String getDZByRecyclerPosition(int position) {
        if (position < 0 || position >= gongweiDizhi.length) return "";
        return gongweiDizhi[position];
    }
}
