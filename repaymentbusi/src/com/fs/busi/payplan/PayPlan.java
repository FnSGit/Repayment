/**
 * Copyright(C),2018-2018, FngS科技有限责任公司
 * Author:Fshuai
 * Date:2018/11/21 0021 下午 23:44
 * Description:生产还款计划
 */
package com.fs.busi.payplan;

import com.fs.constant.JihuaParam;
import com.fs.constants.BusiEnum;
import com.fs.dao.HklsDao;
import com.fs.dao.HolidayDao;
import com.fs.entity.param.PayParam;
import com.fs.generate.target.entity.YizhiFkxxObj;
import com.fs.generate.target.entity.YizhiHkjihuaObj;
import com.fs.generate.target.entity.YizhiHklsxxObj;
import com.fs.util.common.CommUtil;
import com.fs.util.date.DateUtil;
import com.fs.util.db.DataBase;
import com.fs.util.log.FsLogger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fs.busi.RepayTool.calWyjin;
import static com.fs.busi.RepayTool.moPingYjk;


public class PayPlan {

    private FsLogger logger = FsLogger.getLogger(this.getClass().getName());
    private YizhiHkjihuaObj lastHkjh;
    private String dbpool;
    private Statement statement;

    public PayPlan(String dbpool, Statement statement) {
        this.dbpool = dbpool;
        this.statement = statement;
    }

    /**
     * 根据还款方式获得不同还款计划
     *
     * @param fkxx 放款信息
     * @return
     */
    public List<YizhiHkjihuaObj> getPayPlan(YizhiFkxxObj fkxx) {
        /*
         * 计划信息初始化
         */

        List<YizhiHkjihuaObj> lstHkjh = new ArrayList<>();


			/*//669订单
			JihuaParam param=get669Plan(fkxx.getHxfs());
			lstHkjh=getHkRiqi2(fkxx, lstHkjh, param);*/

        //506 568订单
        PayParam payParam = new PayParam(fkxx);


        switch (payParam.getSpecialPro()) {
            case JihuaParam.specialPro1:
            case JihuaParam.specialPro2:
                if (payParam.getKouxiFs() == JihuaParam.kouxiFs2) {
//					lstHkjh=getHkRiqi4(fkxx, param);
                } else if (payParam.getKouxiFs() == JihuaParam.kouxiFs1) {
//					lstHkjh=getHkRiqi5(fkxx, param);
                }
                break;

            default:
                if (payParam.getKouxiFs() == JihuaParam.kouxiFs2) {
                    lstHkjh = new ShangkouPlan(payParam).getPlan(fkxx);
                } else if (payParam.getKouxiFs() == JihuaParam.kouxiFs1) {
//					lstHkjh=getHkRiqi2(fkxx, lstHkjh, param);
                }
                break;
        }


        return lstHkjh;
    }

    /**
     * 还款计划订单入表
     *
     * @param lstHkjihua
     */


    public void insertPlan(List<YizhiHkjihuaObj> lstHkjihua, YizhiFkxxObj fkxx) {

        lastHkjh = null;
//        HkjhDao hkjhDao = new HkjhDao(dbpool);
        for (int i = 0; i < lstHkjihua.size(); i++) {
            //初始化
            YizhiHkjihuaObj hkjihua = lstHkjihua.get(i);
            hkjihua.setDqsfjqbz(BusiEnum.NO.value);
            String plfzkey = fkxx.getPlfzuhao() + fkxx.getOrderno() + hkjihua.getQici();
            BigInteger bigintKey = BigInteger.valueOf(plfzkey.hashCode());
            if (BusiEnum.YES.value.equals(fkxx.getShifcuoq())) {

                /*20日放款错期 */
                if (fkxx.getFkrq().substring(6, 8).equals("20")) {
                    if (hkjihua.getQici().equals("0")) {
                        lastHkjh = hkjihua;
                        continue;
                    } else if (hkjihua.getQici().equals("1")) {
                        hkjihua.setKsriqi(lastHkjh.getKsriqi());
                        hkjihua.setYhkriqi(DateUtil.getNextMonth(lastHkjh.getKsriqi(), 1));
                        hkjihua.setJsriqi(DateUtil.getNextMonth(lastHkjh.getKsriqi(), 1));
                        hkjihua.setYinghklx(new BigDecimal(lastHkjh.getYinghklx()).add(new BigDecimal(hkjihua.getYinghklx())).toString());
                        hkjihua.setYinghklx(new BigDecimal(lastHkjh.getYinghkbj()).add(new BigDecimal(hkjihua.getYinghkbj())).toString());
                        hkjihua.setYhfwfee(new BigDecimal(lastHkjh.getYhfwfee()).add(new BigDecimal(hkjihua.getYhfwfee())).toString());
                        hkjihua.setYhqdffee(new BigDecimal(lastHkjh.getYhqdffee()).add(new BigDecimal(hkjihua.getYhqdffee())).toString());
                        hkjihua.setYhfee(new BigDecimal(lastHkjh.getYhfee()).add(new BigDecimal(hkjihua.getYhfee())).toString());
                        lastHkjh = hkjihua;
                    } else {
                        hkjihua.setJsriqi(hkjihua.getYhkriqi());
                        if (lastHkjh != null) {
                            hkjihua.setKsriqi(lastHkjh.getJsriqi());
                        }
                    }
                    hkjihua.setQici(String.valueOf(i - 1));

                } else {
                    hkjihua.setJsriqi(hkjihua.getYhkriqi());
                    if (lastHkjh != null) {
                        hkjihua.setKsriqi(lastHkjh.getJsriqi());
                    }
                }

                hkjihua.setJsriqi(hkjihua.getYhkriqi());
                if (lastHkjh != null) {
                    hkjihua.setKsriqi(lastHkjh.getJsriqi());
                }
                lastHkjh = hkjihua;
            }
            hkjihua.setPlfzuhao(bigintKey.divideAndRemainder(BigInteger.valueOf(100))[1].toString());
            try {
//                FsLogger logger = FsLogger.getLogger(this.getClass().getName());
//                logger.debug("insertSql:{}", PrepareSql.insertSql(hkjihua, "yizhi_hkjihua"));
                DataBase.insert(statement, hkjihua, "yizhi_hkjihua");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void payDetail(List<YizhiHkjihuaObj> lstHkjihua, YizhiFkxxObj fkxxObj) {

        PayParam payParam = new PayParam(fkxxObj);
        String sOrderNo = fkxxObj.getOrderno();
        double dfkje = Double.parseDouble(fkxxObj.getFkje());
        int hxfs = Integer.parseInt(fkxxObj.getHxfs());
        int kouxifs = Integer.parseInt(fkxxObj.getKouxifs());
        Map<String, YizhiHkjihuaObj> mapWHkjihua = new HashMap<>();//未还清期次
        Map<String, YizhiHkjihuaObj> mapGQHkjihua = new HashMap<>();//违约金挂起期次

        String zero = "0.00";
        BigDecimal zerBigDecimal = BigDecimal.ZERO;
        String sHkriqi = "";//还款流水日期记录
        double dSurplus = 0;//上期多还剩余金额
        Map<String, BigDecimal> mapjmSurplus = new HashMap<>();//溢缴减免金额
        Map<String, BigDecimal> mapgqSurplus = new HashMap<>();//溢缴挂起金额
        int hkSeq = 0;//还款流水序号游标

        HklsDao hklsDao = new HklsDao(dbpool, statement);
        HolidayDao holidayDao = new HolidayDao(dbpool, statement);

        List<YizhiHklsxxObj> lstHklsxxAll = null;
        try {
            lstHklsxxAll = hklsDao.sel_hkls("orderno", sOrderNo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<YizhiHklsxxObj> lstHklsxx = new ArrayList<>();
        List<YizhiHklsxxObj> lstHklsxxJm = new ArrayList<>();
        //流水拆分
        for (YizhiHklsxxObj hklsxx : lstHklsxxAll) {
            if (hklsxx.getDanqjmfs() == null) {
                lstHklsxx.add(hklsxx);
            } else {
                lstHklsxxJm.add(hklsxx);
                if (Integer.parseInt(hklsxx.getDanqjmfs()) == JihuaParam.wyjjmzc) {
                    mapjmSurplus.put(hklsxx.getHkriqi(), new BigDecimal(hklsxx.getHkjine()));
                } else if (Integer.parseInt(hklsxx.getDanqjmfs()) == JihuaParam.wyjjmgq) {
                    mapgqSurplus.put(hklsxx.getHkriqi(), new BigDecimal(hklsxx.getHkjine()));
                }

            }
        }
        if (CommUtil.isNull(lstHklsxx)) {
            logger.debug("还款流水信息表未查到数据，订单号：{}。接着处理下一条订单。", sOrderNo);
            return;
        }
        int iHklsSize = lstHklsxx.size();//还款信息总流水数
        for (int i = 0; i < lstHkjihua.size(); ++i) {
            YizhiHkjihuaObj hkjihua = lstHkjihua.get(i);
//            hkjihua.initUniqIndx("orderno","qici");
            String sYhRiqi = hkjihua.getYhkriqi();//应还款日期
            double dYhBenj = Double.parseDouble(hkjihua.getYinghkbj());//应还款本金
            double dYhLixi = Double.parseDouble(hkjihua.getYinghklx());//应还款利息
            double dYhFee = Double.parseDouble(hkjihua.getYhfee());//应还费用
            double dyhfwFee = Double.parseDouble(hkjihua.getYhfwfee());
            double dyhqdfFee = Double.parseDouble(hkjihua.getYhqdffee());

            double dyhSum = dYhBenj + dYhLixi + dYhFee;//应还总金额
            //精度调整为2
            dyhSum = new BigDecimal(dyhSum).setScale(2, RoundingMode.HALF_UP).doubleValue();
//					bizlog.debug("应还总金额为：%s", dyhSum);

            double dHkJine = 0;//实际还款总金额
            if (dyhSum == 0) {
                hkjihua.setDqsfjqbz(BusiEnum.YES.value);
                try {
                    DataBase.update(statement, hkjihua, "YIZHI_HKJIHUA", hkjihua.getUniqueIndx());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            while (dyhSum > 0) {//本期没有应还金额则跳过流水匹配
                //获取流水信息
                YizhiHklsxxObj hklsxx = null;
                if (hkSeq < iHklsSize) {
                    if (hkSeq > 0)
                        //溢缴款大于当还，则表示上次流水结清当期
                        if (dSurplus >= dyhSum)
                            --hkSeq;
                    hklsxx = lstHklsxx.get(hkSeq);
                } else {
                    hklsxx = lstHklsxx.get(iHklsSize - 1);
                    if (dSurplus != 0) {
                        hklsxx.setHkjine(zero);
                    } else {
                        if (dHkJine > 0) {// 未还够本期应还金额，且没有后续流水
                            sHkriqi = "";
                            hkjihua.setDqsfjqbz(BusiEnum.NO.value);
                            if (dHkJine >= dyhqdfFee) {
                                dHkJine -= dyhqdfFee;
                                hkjihua.setShihqdffee((new BigDecimal(
                                        dyhqdfFee)).setScale(2,
                                        RoundingMode.HALF_UP).toString());
                                if (dHkJine >= dyhfwFee) {
                                    dHkJine -= dyhfwFee;
                                    hkjihua.setShihfwfee(new BigDecimal(
                                            dyhfwFee).setScale(2,
                                            RoundingMode.HALF_UP).toString());
                                    if (dHkJine >= dYhLixi) {
                                        hkjihua.setShhklixi(BigDecimal.valueOf(dYhLixi).setScale(2, RoundingMode.HALF_UP).toString());// 还利息
                                        if (dHkJine < dYhBenj + dYhLixi) {
                                            hkjihua.setShhkbenj(BigDecimal.valueOf(dHkJine - dYhLixi).setScale(2, RoundingMode.HALF_UP).toString());// 还部分金额
                                        } else if (dHkJine > dYhBenj + dYhLixi) {
                                            sHkriqi = hklsxx.getHkriqi();
                                            // 计算违约金
                                            BigDecimal lwyjin = calWyjin(payParam, sHkriqi, sYhRiqi, i, holidayDao);
                                            hkjihua.setYhwyj(lwyjin.setScale(2, RoundingMode.HALF_UP).toString());
                                            hkjihua.setShhkbenj(BigDecimal.valueOf(dYhBenj).setScale(2, RoundingMode.HALF_UP).toString());// 还本金金额
                                            hkjihua.setShihfee(BigDecimal.valueOf(dHkJine - dYhBenj - dYhLixi).setScale(2, RoundingMode.HALF_UP).toString());// 换部分费用
                                        }
                                    } else if (dHkJine < dYhLixi) {
                                        hkjihua.setShhklixi(BigDecimal
                                                .valueOf(dHkJine)
                                                .setScale(
                                                        2,
                                                        RoundingMode.HALF_UP).toString());// 还部分利息
                                    }
                                } else {
                                    hkjihua.setShihfwfee(new BigDecimal(
                                            dHkJine).setScale(2,
                                            RoundingMode.HALF_UP).toString());
                                }
                            } else {
                                hkjihua.setShihqdffee((new BigDecimal(
                                        dyhqdfFee)).setScale(2,
                                        RoundingMode.HALF_UP).toString());
                            }

                            hkjihua.setHkriqi(sHkriqi);
                            try {
                                DataBase.update(statement, hkjihua, hkjihua.getTable(), hkjihua.getUniqueIndx());// 更新还款计划
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            mapWHkjihua.put(hkjihua.getOrderno() + "-" + hkjihua.getQici(), hkjihua);
                        }
                        break;
                    }
                }

                dHkJine += dSurplus;
                if (dHkJine < dyhSum)
                    dHkJine += Double.parseDouble(hklsxx.getHkjine());

                dSurplus = 0;//清空剩余金额

//							System.out.println("期次："+hkjihua.getQici()+"还款金额："+dHkJine);
                //精度调整为2
                dHkJine = new BigDecimal(dHkJine).setScale(2, RoundingMode.HALF_UP).doubleValue();

                if (dHkJine == dyhSum) {//还够利息加本金及费用情况
                    sHkriqi = hklsxx.getHkriqi();
                    hkjihua.setHkriqi(sHkriqi);
                    ++hkSeq;
                    //抹平利息及本金
                    hkjihua.setShhkbenj(hkjihua.getYinghkbj());
                    hkjihua.setShhklixi(hkjihua.getYinghklx());
                    hkjihua.setShihfee(hkjihua.getYhfee());
                    hkjihua.setShihfwfee(hkjihua.getYhfwfee());
                    hkjihua.setShihqdffee(hkjihua.getYhqdffee());
                    //计算违约金
                    BigDecimal lwyjin = calWyjin(payParam, sHkriqi, sYhRiqi, i, holidayDao);
                    hkjihua.setYhwyj(lwyjin.setScale(2, RoundingMode.HALF_UP).toString());

                    if (lwyjin.compareTo(zerBigDecimal) > 0) {
//									hkjihua.setDqsfjqbz(E_SHIFOUBZ.NO);
                        mapWHkjihua.put(hkjihua.getOrderno() + "-" + hkjihua.getQici(), hkjihua);
                    } else {
                        hkjihua.setDqsfjqbz(BusiEnum.YES.value);
                    }

                    try {
                        DataBase.update(statement, hkjihua, hkjihua.getTable(), hkjihua.getUniqueIndx());// 更新还款计划
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                } else if (dHkJine > dyhSum) {//多还情况
                    sHkriqi = hklsxx.getHkriqi();
                    hkjihua.setHkriqi(sHkriqi);
                    ++hkSeq;
                    //抹平利息及本金
                    hkjihua.setShhkbenj(hkjihua.getYinghkbj());
                    hkjihua.setShhklixi(hkjihua.getYinghklx());
                    hkjihua.setShihfee(hkjihua.getYhfee());
                    hkjihua.setShihfwfee(hkjihua.getYhfwfee());
                    hkjihua.setShihqdffee(hkjihua.getYhqdffee());
                    //计算违约金
                    BigDecimal lwyjin = calWyjin(payParam, sHkriqi, sYhRiqi, i, holidayDao);
                    hkjihua.setYhwyj(lwyjin.setScale(2, RoundingMode.HALF_UP).toString());

                    //多余金额，计算实还违约金
                    double dExtra = dHkJine - dyhSum;
                    if (JihuaParam.specialPro4 == payParam.getSpecialPro()) {//不抵扣违约金
                        if (lwyjin.compareTo(zerBigDecimal) == 0) {
                            hkjihua.setDqsfjqbz(BusiEnum.YES.value);
                        }
                        dSurplus = dExtra;
                    } else {
                        double dlwyjin = lwyjin.doubleValue();
                        if (dExtra == dlwyjin) {//抹平违约金
                            hkjihua.setDqsfjqbz(BusiEnum.YES.value);
                            hkjihua.setShihwyjn(lwyjin.setScale(2, RoundingMode.HALF_UP).toString());
                        } else if (dExtra < dlwyjin) {//不够还违约金
                            //								hkjihua.setDqsfjqbz(E_SHIFOUBZ.NO);
                            mapWHkjihua.put(hkjihua.getOrderno() + "-" + hkjihua.getQici(), hkjihua);
                            hkjihua.setShihwyjn(BigDecimal.valueOf(dExtra).setScale(2, RoundingMode.HALF_UP).toString());
                        } else {//抹平违约金后剩余
                            hkjihua.setShihwyjn(lwyjin.setScale(2, RoundingMode.HALF_UP).toString());
                            hkjihua.setDqsfjqbz(BusiEnum.YES.value);
                            dSurplus = dExtra - dlwyjin;

                        }
                    }
                    if (i == lstHkjihua.size() - 1) {
                        hkjihua.setYijiaok(new BigDecimal(dSurplus).setScale(2, RoundingMode.HALF_UP).toString());
                    }
                    try {
                        DataBase.update(statement, hkjihua, hkjihua.getTable(), hkjihua.getUniqueIndx());// 更新还款计划
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                } else {
                    ++hkSeq;//金额不够，查看下一条
                }


            }
            //剩余溢缴款+多余流水溢缴款
//					if(i==lstHkjihua.size()-1&&hkSeq<iHklsSize){
            if (i == lstHkjihua.size() - 1) {

                for (int j = hkSeq; j < iHklsSize; j++) {
                    YizhiHklsxxObj hklsxx = lstHklsxx.get(j);
                    dSurplus += Double.parseDouble(hklsxx.getHkjine());
                }
                double surpRest[] = null;
                if (dSurplus > 0) {
                    //抹平渠道返费
                    if (hkjihua.getYhqdffee().compareTo(hkjihua.getShihqdffee()) > 0) {
                        surpRest = moPingYjk(new BigDecimal(hkjihua.getYhqdffee()), new BigDecimal(hkjihua.getShihqdffee()), dSurplus);
                        if (surpRest[0] == 0) {
                            hkjihua.setShihqdffee(hkjihua.getYhqdffee());
                            dSurplus = surpRest[1];

                        } else if (surpRest[0] == 1) {
                            hkjihua.setShihqdffee(new BigDecimal(hkjihua.getShihqdffee()).add(new BigDecimal(dSurplus)).setScale(2, RoundingMode.HALF_UP).toString());
                            dSurplus = 0;

                        }
                    }
                    //抹平服务费
                    if (hkjihua.getYhfwfee().compareTo(hkjihua.getShihfwfee()) > 0) {
                        surpRest = moPingYjk(new BigDecimal(hkjihua.getYhfwfee()), new BigDecimal(hkjihua.getShihfwfee()), dSurplus);
                        if (surpRest[0] == 0) {
                            hkjihua.setShihfwfee(hkjihua.getYhfwfee());
                            dSurplus = surpRest[1];
                        } else if (surpRest[0] == 1) {
                            hkjihua.setShihfwfee(new BigDecimal(hkjihua.getShihfwfee()).add(new BigDecimal(dSurplus)).setScale(2, RoundingMode.HALF_UP).toString());
                            dSurplus = 0;
                        }
                    }
                    //抹平利息
                    if (hkjihua.getYinghklx().compareTo(hkjihua.getShhklixi()) > 0) {
                        surpRest = moPingYjk(new BigDecimal(hkjihua.getYinghklx()), new BigDecimal(hkjihua.getShhklixi()), dSurplus);
                        if (surpRest[0] == 0) {
                            hkjihua.setShhklixi(hkjihua.getYinghklx());
                            dSurplus = surpRest[1];
                        } else if (surpRest[0] == 1) {
                            hkjihua.setShhklixi(new BigDecimal(hkjihua.getShhklixi()).add(new BigDecimal(dSurplus)).setScale(2, RoundingMode.HALF_UP).toString());
                            dSurplus = 0;
                        }
                    }
                    //抹平本金
                    if (hkjihua.getYinghkbj().compareTo(hkjihua.getShhkbenj()) > 0) {
                        surpRest = moPingYjk(new BigDecimal(hkjihua.getYinghkbj()), new BigDecimal(hkjihua.getShhkbenj()), dSurplus);
                        if (surpRest[0] == 0) {
                            hkjihua.setShhkbenj(hkjihua.getYinghkbj());
                            dSurplus = surpRest[1];
                        } else if (surpRest[0] == 1) {
                            hkjihua.setShhkbenj(new BigDecimal(hkjihua.getShhkbenj()).add(new BigDecimal(dSurplus)).setScale(2, RoundingMode.HALF_UP).toString());
                            dSurplus = 0;
                        }
                    }
                    //抹平违约金
                    if (hkjihua.getYhwyj().compareTo(hkjihua.getShihwyjn()) > 0) {
                        surpRest = moPingYjk(new BigDecimal(hkjihua.getYhwyj()), new BigDecimal(hkjihua.getShihwyjn()), dSurplus);
                        if (surpRest[0] == 0) {
                            hkjihua.setShihwyjn(hkjihua.getYhwyj());
                            dSurplus = surpRest[1];
                        } else if (surpRest[0] == 1) {
                            hkjihua.setShihwyjn(new BigDecimal(hkjihua.getShihwyjn()).add(new BigDecimal(dSurplus)).setScale(2, RoundingMode.HALF_UP).toString());
                            dSurplus = 0;
                        }
                    }
                }

                hkjihua.setYijiaok(new BigDecimal(dSurplus).setScale(2, RoundingMode.HALF_UP).toString());
                if (new BigDecimal(dyhSum).compareTo(new BigDecimal(hkjihua.getShhklixi()).add(new BigDecimal(hkjihua.getShhkbenj())).add(new BigDecimal(hkjihua.getShihfee()))) == 0
                        && hkjihua.getYhwyj().compareTo(hkjihua.getShihwyjn()) == 0) {
                    hkjihua.setDqsfjqbz(BusiEnum.YES.value);
                    //最后一期记录
                    mapWHkjihua.put(hkjihua.getOrderno() + "-" + hkjihua.getQici(), hkjihua);
                } else {
                    mapWHkjihua.put(hkjihua.getOrderno() + "-" + hkjihua.getQici(), hkjihua);
                }

                try {
                    DataBase.update(statement, hkjihua, hkjihua.getTable(), hkjihua.getUniqueIndx());// 更新还款计划
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        //减免金额处理
        for (Map.Entry<String, YizhiHkjihuaObj> mapEntryhkjihua : mapWHkjihua.entrySet()) {
            YizhiHkjihuaObj hkjihua = mapEntryhkjihua.getValue();
            BigDecimal jmBigDecimal = zerBigDecimal;
            BigDecimal gqBigDecimal = zerBigDecimal;
            if (hkjihua.getDqsfjqbz().equals(BusiEnum.NO.value) ) {
                for (YizhiHklsxxObj hklsxx : lstHklsxxJm) {
                    if (JihuaParam.wyjjmzc == Integer.parseInt(hklsxx.getDanqjmfs()) && hkjihua.getHkriqi().equals(hklsxx.getHkriqi())) {
                        jmBigDecimal = mapjmSurplus.get(hkjihua.getHkriqi());
                        BigDecimal sjmBigDecimal = new BigDecimal(hkjihua.getYhwyj()).subtract(new BigDecimal(hkjihua.getShihwyjn()));
                        if (jmBigDecimal.compareTo(sjmBigDecimal) <= 0) {
                            hkjihua.setZcjmfaxi(jmBigDecimal.setScale(2, RoundingMode.HALF_UP).toString());
                            if (new BigDecimal(hkjihua.getYhwyj()).compareTo(new BigDecimal(hkjihua.getShihwyjn()).add(jmBigDecimal)) == 0) {
                                hkjihua.setDqsfjqbz(BusiEnum.YES.value);
                            }
                            mapjmSurplus.put(hklsxx.getHkriqi(), zerBigDecimal);
                        } else {
                            hkjihua.setZcjmfaxi(sjmBigDecimal.setScale(2, RoundingMode.HALF_UP).toString());
                            if (new BigDecimal(hkjihua.getYhwyj()).compareTo(new BigDecimal(hkjihua.getShihwyjn()).add(sjmBigDecimal)) == 0) {
                                hkjihua.setDqsfjqbz(BusiEnum.YES.value);
                            }
                            mapjmSurplus.put(hklsxx.getHkriqi(), jmBigDecimal.subtract(sjmBigDecimal));
                        }
                        try {
                            DataBase.update(statement, hkjihua, hkjihua.getTable(), hkjihua.getUniqueIndx());// 更新还款计划
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else if (hkjihua.getHkriqi().equals(hklsxx.getHkriqi()) && JihuaParam.wyjjmgq == Integer.parseInt(hklsxx.getDanqjmfs())) {
                        gqBigDecimal = mapgqSurplus.get(hkjihua.getHkriqi());
                        BigDecimal sgqBigDecimal = new BigDecimal(hkjihua.getYhwyj()).subtract(new BigDecimal(hkjihua.getShihwyjn()));
                        if (gqBigDecimal.compareTo(sgqBigDecimal) <= 0) {
                            hkjihua.setWyjguaqi(gqBigDecimal.setScale(2, RoundingMode.HALF_UP).toString());
//
                            mapGQHkjihua.put(hkjihua.getOrderno() + "-" + hkjihua.getQici(), hkjihua);
                            mapgqSurplus.put(hklsxx.getHkriqi(), zerBigDecimal);
                        } else {
                            hkjihua.setWyjguaqi(sgqBigDecimal.setScale(2, RoundingMode.HALF_UP).toString());
//
                            mapGQHkjihua.put(hkjihua.getOrderno() + "-" + hkjihua.getQici(), hkjihua);
                            mapgqSurplus.put(hklsxx.getHkriqi(), gqBigDecimal.subtract(sgqBigDecimal));//预挂起溢出废弃
                        }
                        try {
                            DataBase.update(statement, hkjihua, hkjihua.getTable(), hkjihua.getUniqueIndx());// 更新还款计划
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //保留最后一期更改
                if (hkjihua.getQici().equals(lstHkjihua.get(lstHkjihua.size() - 1).getQici())) {
                    mapWHkjihua.put(hkjihua.getOrderno() + "-" + hkjihua.getQici(), hkjihua);
                }
            }

        }
        //最后一期还清挂起期次
        YizhiHkjihuaObj lastHkjihua = mapWHkjihua.get(sOrderNo + "-" + lstHkjihua.get(lstHkjihua.size() - 1).getQici());
        BigDecimal yijiaoBigDecimal = new BigDecimal(lastHkjihua.getYijiaok());
        if (yijiaoBigDecimal.compareTo(zerBigDecimal) > 0) {
            for (Map.Entry<String, YizhiHkjihuaObj> mapEntryhkjihua : mapGQHkjihua.entrySet()) {
                YizhiHkjihuaObj hkjihua = mapEntryhkjihua.getValue();
                yijiaoBigDecimal = yijiaoBigDecimal.subtract(new BigDecimal(hkjihua.getWyjguaqi()));
                if (yijiaoBigDecimal.compareTo(zerBigDecimal) >= 0) {
                    hkjihua.setDqsfjqbz(BusiEnum.YES.value);
                    try {
                        DataBase.update(statement, hkjihua, hkjihua.getTable(), hkjihua.getUniqueIndx());// 更新还款计划
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            //剩余溢缴款记录
            if (yijiaoBigDecimal.compareTo(zerBigDecimal) > 0) {
                lastHkjihua.setYijiaok(yijiaoBigDecimal.toString());
            }
        }
        //最后一期还清挂起期次

        //统计溢缴减免
        BigDecimal jmBigDecimal = zerBigDecimal;
        BigDecimal gqBigDecimal = zerBigDecimal;
        for (Map.Entry<String, BigDecimal> jmEntry : mapjmSurplus.entrySet()) {
            jmBigDecimal = jmBigDecimal.add(jmEntry.getValue());
        }
        //统计溢缴挂起
        for (Map.Entry<String, BigDecimal> gqEntry : mapgqSurplus.entrySet()) {
            gqBigDecimal = gqBigDecimal.add(gqEntry.getValue());
        }
        lastHkjihua.setYjwyjnjm(jmBigDecimal.toString());
        lastHkjihua.setYjwyjngq(gqBigDecimal.toString());
        try {
            DataBase.update(statement, lastHkjihua, lastHkjihua.getTable(), lastHkjihua.getUniqueIndx());// 更新还款计划
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
