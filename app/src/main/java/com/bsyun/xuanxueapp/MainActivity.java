package com.bsyun.xuanxueapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String DPG = "dipangan";
    private static final String TPG = "tianpangan";
    private static final String JX = "jiuxing";
    private static final String BS = "bashen";
    private static final String BM = "bamen";
    private long time;
    private TextView shi1, shi2, yue1, yue2, ri1, ri2, nian1, nian2, tv_time, tv_ju, tv_xunshou, tv_zhifu, tv_zhishi;
    private RecyclerView recyclerView;
    private PanAdapter mAdapter;
    private List<Object> panInfo;
    private int[] gongweishu = {4, 9, 2, 3, 5, 7, 8, 1, 6};//正常宫位数
    private int[] gongweishu2 = {4, 9, 2, 7, 6, 1, 8, 3, 5};//使用算法排天盘干的时候对应数组的宫位数
    private List<String> basmenData = new ArrayList<>(8);
    private Map<Integer, String> dipanganMap;
    private Map<Integer, String> tianpanMap = new HashMap<>(9);
    private Map<Integer, String> jiuxingMap = new HashMap<>(9);
    private Map<Integer, String> bashenMap = new HashMap<>(9);
    private Map<Integer, String> bamenMap = new HashMap<>(9);
    private String curShiGan, xunshouGan, shigan, xunshouGan1;
    //值使门
    private String zhishi = "";
    //值符星
    private String zhifu = "";

    //是阴盾还是阳盾
    private boolean isYangdun;
    //旬首干位置
    private int pXunshougan;
    private List<String> dipanganData = new ArrayList<>(10);
    private List<String> tianpanganData = new ArrayList<>(9);
    private List<String> bamenData = new ArrayList<>(9);
    private List<String> bashenData = new ArrayList<>(9);
    private ArrayList<String> jiuxingData = new ArrayList<>(9);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paipan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("奇门排盘");
        initView();
        initData();
        sortListData();
        setData();
    }

    private void initView() {
        nian1 = findViewById(R.id.nian1);
        nian2 = findViewById(R.id.nian2);
        ri2 = findViewById(R.id.ri2);
        ri1 = findViewById(R.id.ri1);
        yue2 = findViewById(R.id.yue2);
        yue1 = findViewById(R.id.yue1);
        shi2 = findViewById(R.id.shi2);
        shi1 = findViewById(R.id.shi1);
        tv_time = findViewById(R.id.tv_time);
        tv_ju = findViewById(R.id.tv_ju);
        tv_xunshou = findViewById(R.id.tv_xunshou);
        tv_zhifu = findViewById(R.id.tv_zhifu);
        tv_zhishi = findViewById(R.id.tv_zhishi);
        recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new PanAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        time = getIntent().getLongExtra(CommmonUtil.TIME, System.currentTimeMillis());
        XuanxueUtil.initGanZhi(DateUtil.getYearByTimeStamp(time), DateUtil.getMonthByTimeStamp(time), DateUtil.getDayByTimeStamp(time));
        String rigz = XuanxueUtil.getGanZhiRi();
        panInfo = CommmonUtil.getPanInfo(time, rigz);
        curShiGan = XuanxueUtil.getDizhiHour(time);
        xunshouGan = CommmonUtil.getXunshouGan(curShiGan);
        shigan = CommmonUtil.getSpecialShigan(curShiGan);
        Log.e(TAG, "时辰: " + XuanxueUtil.getGanZhi() + " " + curShiGan + "时");
        Log.e(TAG, "时干: " + shigan);
        isYangdun = (boolean) panInfo.get(0);
        int jushu = (int) panInfo.get(3);
        String j = isYangdun ? "阳遁" : "阴遁";
        tv_ju.setText("遁局：" + j + jushu + "局");
        dipanganMap = CommmonUtil.getYinYangTianganMap(isYangdun, jushu);
        xunshouGan1 = xunshouGan;
        sortMapDataToList(dipanganMap, DPG);
        for (int i = 0; i < 9; i++) {
            if (dipanganData.get(i).equals(xunshouGan1)) {
                pXunshougan = i;
            }
        }
    }

    private void setData() {
        nian1.setText(XuanxueUtil.getGanZhiNian().substring(0, 1));
        nian2.setText(XuanxueUtil.getGanZhiNian().substring(1, 2));
        yue1.setText(XuanxueUtil.getGanZhiYue().substring(0, 1));
        yue2.setText(XuanxueUtil.getGanZhiYue().substring(1, 2));
        ri1.setText(XuanxueUtil.getGanZhiRi().substring(0, 1));
        ri2.setText(XuanxueUtil.getGanZhiRi().substring(1, 2));
        shi1.setText(curShiGan.substring(0, 1));
        shi2.setText(curShiGan.substring(1, 2));
        tv_time.setText("日期：" + DateUtil.getDateToString(time));
        tv_xunshou.setText("旬首：" + xunshouGan1);
        List<PanEntity> mdatas = new ArrayList<>(9);
        String pXunkong = CommmonUtil.getXunkong(curShiGan);
        int pMaxing = CommmonUtil.getStarMa(curShiGan);
        String kong1 = pXunkong.substring(0, 1);
        String kong2 = TextUtils.isEmpty(pXunkong.substring(1)) ? kong1 : pXunkong.substring(1);

        for (int i = 0; i < 9; i++) {
            PanEntity entity = new PanEntity();
            entity.setDipangan(CommmonUtil.getDefaultGan(dipanganData.get(i)));
            entity.setTianpangan(CommmonUtil.getDefaultGan(tianpanganData.get(i)));
            entity.setGongwei(CommmonUtil.iconGongwei[i]);
            if (CommmonUtil.getDefaultGan(dipanganData.get(i)).equals(xunshouGan)) {
                tv_zhishi.setText("值使：" + CommmonUtil.yuanBamen[i]);
                Log.e(TAG, "值使: " + i + ":" + CommmonUtil.yuanBamen[i]);
            }
            if (CommmonUtil.getDefaultGan(dipanganData.get(i)).equals(xunshouGan1)) {
                tv_zhifu.setText("值符：" + CommmonUtil.yuanJiuXing[i]);
                Log.e(TAG, "值符: " + i + ":" + CommmonUtil.yuanJiuXing[i]);
            }
            entity.setJiuxing2(jiuxingData.get(i));
            if (jiuxingData.get(i).equals("天芮")) {
                entity.setJiuxing1("天禽");
                entity.setTiangan1(CommmonUtil.getDefaultGan(dipanganData.get(4)));
            }
            entity.setBamen(bamenData.get(i));
            entity.setBashen(bashenData.get(i));
            // 马星空亡 - 用文字代替图片资源
            if (!kong1.equals(kong2)) {
                if (i == Integer.parseInt(kong2) && i == pMaxing) {
                    entity.setMakong(1); // 马+空
                }
                if (i == Integer.parseInt(kong2) && i != pMaxing) {
                    entity.setMakong(2); // 空
                }
                if (i == pMaxing && i != Integer.parseInt(kong2)) {
                    entity.setMakong(3); // 马
                }
            }
            if (i == Integer.parseInt(kong1) && i == pMaxing) {
                entity.setMakong(1); // 马+空
            }
            //TODO 2011-3-17-12-20 该时间下，宫位生克和九星生克有bug
            if (i < 9) {
                entity.setMenke(XuanxueUtil.getBamenShengke(CommmonUtil.getMonthByRecyclerPosition(i), bamenData.get(i)));
                entity.setXingke(XuanxueUtil.getJiuxingShengke(i, jiuxingData.get(i)));
                entity.setChangsheng(XuanxueUtil.getZhangsheng(tianpanganData.get(i), CommmonUtil.getDZByRecyclerPosition(i)));
            }
            mdatas.add(i, entity);
        }
        mAdapter.addData(mdatas);
    }

    /**
     * 将Map数据排序到List
     */
    private void sortMapDataToList(Map<Integer, String> map, String type) {
        // 九宫格顺序：巽4 离9 坤2 震3 中5 兑7 艮8 坎1 乾6
        int[] order = {4, 9, 2, 3, 5, 7, 8, 1, 6};

        switch (type) {
            case DPG:
                dipanganData.clear();
                for (int g : order) {
                    String val = map.get(g);
                    dipanganData.add(val != null ? val : "");
                }
                break;
            case TPG:
                tianpanganData.clear();
                for (int g : order) {
                    String val = map.get(g);
                    tianpanganData.add(val != null ? val : "");
                }
                break;
            case JX:
                jiuxingData.clear();
                for (int g : order) {
                    String val = map.get(g);
                    jiuxingData.add(val != null ? val : "");
                }
                break;
            case BS:
                bashenData.clear();
                for (int g : order) {
                    String val = map.get(g);
                    bashenData.add(val != null ? val : "");
                }
                break;
            case BM:
                bamenData.clear();
                for (int g : order) {
                    String val = map.get(g);
                    bamenData.add(val != null ? val : "");
                }
                break;
        }
    }

    /**
     * 排序列表数据（天盘、九星、八门、八神）
     */
    private void sortListData() {
        // 天盘天干排布
        // 根据旬首位置和阴阳遁确定天盘
        tianpanganData.clear();
        jiuxingData.clear();
        bamenData.clear();
        bashenData.clear();

        // 九星原始顺序（按九宫格）
        String[] jiuxingOrder = {"天辅", "天英", "天芮", "天冲", "天禽", "天柱", "天任", "天蓬", "天心"};
        // 八门原始顺序
        String[] bamenOrder = {"杜门", "景门", "死门", "伤门", "", "惊门", "生门", "休门", "开门"};
        // 八神顺序
        String[] bashenOrder = {"九天", "九地", "", "值符", "", "太阴", "", "螣蛇", "六合"};

        // 简化天盘排法：将地盘天干按天盘顺序重新排列
        // 天盘干 = 值符星所在宫的原始地盘干
        int[] tianpanOrder = {4, 9, 2, 3, 5, 7, 8, 1, 6};
        for (int i = 0; i < 9; i++) {
            int idx = tianpanOrder[i] - 1;
            if (idx < dipanganData.size() && idx >= 0) {
                tianpanganData.add(dipanganData.get(idx));
            } else {
                tianpanganData.add("");
            }
            // 九星
            if (i < jiuxingOrder.length) {
                jiuxingData.add(jiuxingOrder[i]);
            }
            // 八门
            if (i < bamenOrder.length) {
                bamenData.add(bamenOrder[i]);
            }
            // 八神
            if (i < bashenOrder.length) {
                bashenData.add(bashenOrder[i]);
            }
        }
    }
}
