/**
 * Copyright(C),2018-2018, FngS科技有限责任公司
 * Author:Fshuai
 * Date:2018/11/24 0024 下午 23:29
 * Description:业务逻辑工具
 */
package com.fs.busi;

import com.fs.constant.FeeEnum;
import com.fs.constant.JihuaParam;
import com.fs.dao.HolidayDao;
import com.fs.entity.BenjinEntity;
import com.fs.entity.BudgetEntity;
import com.fs.entity.FeeEntity;
import com.fs.entity.LixiEntity;
import com.fs.entity.param.DateParam;
import com.fs.entity.param.PayParam;
import com.fs.generate.target.entity.YizhiHkjihuaObj;
import com.fs.generate.target.entity.YizhiHolidayObj;
import com.fs.task.RepaymentVariable;
import com.fs.util.common.CommUtil;
import com.fs.util.date.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.fs.util.date.DateUtil.getNextDate;


public class RepayTool {


    public static String theYearToPay(String fkrq,int month,int jiesFs){
        //月息年本中间期还本日期处理
        String year=DateUtil.getNextMonth(fkrq, month);
        //对日减一处理
        if(jiesFs==JihuaParam.jiesFs3){
            year=getNextDate(year, -1);
        }
        return year;
    }



    public static DateParam buildRiqi(DateParam param, int qiciSegment){
        String jsrq="";
        String ksrq="";
        String yhrq="";

        String fkrq=param.getFkrq();
        String scrq=param.getScrq();
        String ksri=fkrq.substring(6,8);
        String hkri=param.getHkri();
        int jiesFs=param.getJiesFs();
        int kouxiFs=param.getKouxiFs();
        int jixiFs=param.getJixiFs();
        int qixian=param.getQixian();

        switch (qiciSegment) {
            case JihuaParam.qiciLingQi:
                param.ksrq=fkrq;
                param.jsrq=scrq;
                param.yhrq=scrq;
                break;
            case JihuaParam.qiciShouQi:

                if (ksri.compareTo(hkri) == 0) {
                    jsrq = DateUtil.getNextMonth(fkrq, 1);
                } else if (ksri.compareTo(hkri) < 0) {
                    jsrq = fkrq.substring(0, 6) + hkri;
                } else if (ksri.compareTo(hkri) > 0) {
                    jsrq = DateUtil.getNextMonth(fkrq, 1);
                    jsrq = jsrq.substring(0, 6) + hkri;
                }
                //应还日
                switch (kouxiFs) {
                    case JihuaParam.kouxiFs1:
                        yhrq = jsrq;
                        break;
                    case JihuaParam.kouxiFs2:
                        yhrq=scrq;
                        //计息规则变动
                        if(jixiFs==JihuaParam.jixiFs21){//等额还款固定30天区间规则
                            jsrq=DateUtil.getNextMonth(fkrq, 1);
                            break;
                        }
                        if(ksri.compareTo("15")>=0&&ksri.compareTo("20")<0){
                            jsrq = DateUtil.getNextMonth(fkrq, 1);
                            jsrq = jsrq.substring(0, 6) + hkri;
                        }

                        break;
                    default:
                        break;
                }


                param.ksrq=fkrq;
                param.jsrq=jsrq;
                param.yhrq=yhrq;
                break;
            case JihuaParam.qiciMoQi:
                String lastRiqi="";
                lastRiqi=DateUtil.getNextMonth(fkrq,qixian);//结束日期为放款日期的总期限之后
                switch (jiesFs) {
                    case JihuaParam.jiesFs1:
                        lastRiqi=lastRiqi.substring(0,6)+hkri;
                        break;
                    case JihuaParam.jiesFs2:
                        //按月对日，不做处理
                        break;
                    case JihuaParam.jiesFs3:
                        lastRiqi=getNextDate(lastRiqi, -1);//按月对日减一
                        break;
                    default:
                        break;
                }

                param.ksrq=param.jsrq;//开始日期等于上次结束日期
                param.jsrq=lastRiqi;

                if(jixiFs==JihuaParam.jixiFs21){//等额固定30天计息
                    param.yhrq=DateUtil.getNextMonth(param.yhrq, 1);
                    break;
                }
                if(kouxiFs==JihuaParam.kouxiFs1){
                    param.yhrq=param.jsrq;//最后一期应还日=结束日
                }else if (kouxiFs==JihuaParam.kouxiFs2) {
                    param.yhrq=param.ksrq;//最后一期应还日=开始日
                }

                break;
            case JihuaParam.qiciZhongQi:
                ksrq=param.jsrq;//开始日期等于上次结束日期

                jsrq = DateUtil.getNextMonth(ksrq, 1);//结束日期为下月当日
                //应还日
                switch (kouxiFs) {
                    case JihuaParam.kouxiFs1:
                        yhrq=jsrq;//本期应还日等于上期结束日或是本期开始日
                        break;
                    case JihuaParam.kouxiFs2:
                        if(jixiFs==JihuaParam.jixiFs21){//等额固定30天计息
                            yhrq=DateUtil.getNextMonth(param.yhrq, 1);
                            break;
                        }
                        yhrq=ksrq;
                        break;
                    default:
                        break;
                }
                param.ksrq=ksrq;
                param.jsrq=jsrq;
                param.yhrq=yhrq;

                break;
            case JihuaParam.qicijiBen:
                param.yhrq=param.jsrq;
                param.ksrq=fkrq;
            default:
                break;
        }


        return param;
    }

    public static BudgetEntity budget(BudgetEntity budgetEntity, int qicisegment, DateParam riqiParam, LixiEntity lixiEntity, BenjinEntity benjinEntity, FeeEntity feeEntity, int jixiDays){
        double fwfee=0;
        double qdffee=0;
        double fee=0;
        double lixi=0;
        double benj=0;
        boolean koufwf=true;
        boolean kouqdf=true;

        double fkje=budgetEntity.getFkje();
        double fwfLv=feeEntity.feeLvMap.get(FeeEnum.fwfFee);
        double qdffLv=feeEntity.feeLvMap.get(FeeEnum.qdfFee);
        int fwfFs= feeEntity.feeFsMap.get(FeeEnum.fwfFee);
        int qdfFs= feeEntity.feeFsMap.get(FeeEnum.qdfFee);

        switch (qicisegment) {
            case JihuaParam.qiciLingQi:
                //第零期费用一次收取
                if(fwfFs==JihuaParam.feeFs2)
                    koufwf=false;
                if(qdfFs==JihuaParam.feeFs2)
                    kouqdf=false;
                fwfee = calFee(fkje, fwfLv,jixiDays,fwfFs,koufwf);
                qdffee = calFee(fkje,qdffLv,jixiDays,qdfFs,kouqdf);
                fee = fwfee + qdffee;
                //零期利息一次收取
                lixi = calLixi(lixiEntity,JihuaParam.qiciLingQi);
                break;
            case JihuaParam.qiciShouQi:

                lixiEntity.setDays(jixiDays);
                lixi = calLixi(lixiEntity,JihuaParam.qiciShouQi);
                benjinEntity.setDays(jixiDays);
                benjinEntity.setLixi( lixi);
                benj = calBenjin(benjinEntity,riqiParam);

                //计算费用
                switch (lixiEntity.getSpecialPro()) {
                    case JihuaParam.specialPro0:
                        if(fwfFs==JihuaParam.feeFs1)
                            if(lixiEntity.getKouxiFs()==JihuaParam.kouxiFs1)
                                koufwf=false;
                        if(qdfFs==JihuaParam.feeFs1)
                            if(lixiEntity.getKouxiFs()==JihuaParam.kouxiFs1)
                                kouqdf=false;
                        fwfee = calFee(fkje, fwfLv,jixiDays,fwfFs,koufwf);
                        qdffee = calFee(fkje,qdffLv,jixiDays,qdfFs,kouqdf);

                        fee = fwfee + qdffee;
                        break;
                    case JihuaParam.specialPro1:case JihuaParam.specialPro2:
                        fwfee = getFwfee(fwfee, fkje, fwfLv, fwfFs, benjinEntity.getQixian());

                        qdffee = getQdffee(benjinEntity, qdffee, fkje, qdffLv, qdfFs);
                        fee = fwfee + qdffee;
                        break;

                    default:
                        break;
                }

                break;
            case JihuaParam.qiciZhongQi:

                lixi = calLixi(lixiEntity,JihuaParam.qiciZhongQi);//利息每月按30天计算
                benjinEntity.setLixi(lixi);
                benjinEntity.setSpecialPro(lixiEntity.getSpecialPro());
                benj=calBenjin(benjinEntity,riqiParam);
                //计算费用
                if(fwfFs==JihuaParam.feeFs1)
                    koufwf=false;
                if(qdfFs==JihuaParam.feeFs1)
                    kouqdf=false;
                fwfee = calFee(fkje, fwfLv,jixiDays,fwfFs,koufwf);
                qdffee = calFee(fkje,qdffLv,jixiDays,qdfFs,kouqdf);
                fee = fwfee + qdffee;

                break;
            case JihuaParam.qiciMoQi:
                lixiEntity.setDays(jixiDays);
                lixi = calLixi(lixiEntity,JihuaParam.qiciMoQi);

                benjinEntity.setDays(jixiDays);
                benjinEntity.setLixi(lixi);
                if(lixiEntity.getKouxiFs()==JihuaParam.kouxiFs1)
                    benjinEntity.isLast=true;
                benj=calBenjin(benjinEntity,riqiParam);
                //计算费用
                switch (lixiEntity.getSpecialPro()) {
                    case JihuaParam.specialPro0:
                        if(fwfFs==JihuaParam.feeFs1)
                            koufwf=false;
                        if(qdfFs==JihuaParam.feeFs1)
                            kouqdf=false;
                        fwfee = calFee(fkje, fwfLv,jixiDays,fwfFs,koufwf);
                        qdffee = calFee(fkje,qdffLv,jixiDays,qdfFs,kouqdf);
                        fee = fwfee + qdffee;
                        break;
                    case JihuaParam.specialPro1:case JihuaParam.specialPro2:
                        fwfee = getFwfee(fwfee, fkje, fwfLv, fwfFs, budgetEntity.getQixian());

                        qdffee = getQdffee(budgetEntity, qdffee, fkje, qdffLv, qdfFs);
                        fee = fwfee + qdffee;
                        break;

                    default:
                        break;
                }

                break;
            case JihuaParam.qicijiBen:
                benj=calBenjin(benjinEntity,riqiParam);
                break;
            default:

                break;
        }


        budgetEntity.setFwfee(fwfee);
        budgetEntity.setQdffee(qdffee);
        budgetEntity.setFee(fee);
        budgetEntity.setLixi(lixi);
        budgetEntity.setBenj(benj);
        return budgetEntity;
    }

    private static double getQdffee(BudgetEntity budgetEntity, double qdffee, double fkje, double qdffLv, int qdfFs) {
        if(qdfFs==JihuaParam.feeFs2){
            double qdffSum=fkje * (qdffLv / 100.0D) *budgetEntity.getQixian();
            double qdffCharged=0;
            for (YizhiHkjihuaObj hkjh : RepaymentVariable.variable.get().lstHkjh) {
                qdffCharged+=Double.parseDouble(hkjh.getYhqdffee());
            }
            qdffee=qdffSum-qdffCharged;
        }
        return qdffee;
    }

    private static double getFwfee(double fwfee, double fkje, double fwfLv, int fwfFs, int qixian) {
        if (fwfFs == JihuaParam.feeFs2) {
            double fwfSum = fkje * (fwfLv / 100.0D) * qixian;
            double fwfCharged = 0;
            for (YizhiHkjihuaObj hkjh : RepaymentVariable.variable.get().lstHkjh) {
                fwfCharged += Double.parseDouble(hkjh.getYhfwfee());
            }
            fwfee = fwfSum - fwfCharged;
        }
        return fwfee;
    }


    public static double calLixi(LixiEntity lixiEntity, int qiciSegment) {
        int jixiFs=lixiEntity.getJixiFs();
        double fkje=lixiEntity.getFkje();
        double lilv=lixiEntity.getLilv();
        long days=lixiEntity.getDays();
        int qixian=lixiEntity.getQixian();
        int qici=lixiEntity.qici;
        int kouxiFs=lixiEntity.getKouxiFs();
        int shouqi = 0;
        int qiciNext=qici-1;
        if(qiciSegment==JihuaParam.qiciShouQi)
            shouqi=qici;
        if(shouqi==1)
            qiciNext=qici-2;
        double lixiSum ;// 总利息
        long daysSum;// 总天数
        long restDays;// 剩余天数
        long canShu;// 等份参数
        double lixiDengFen;// 每份利息
        //月息年本取十位
        if(jixiFs/10==JihuaParam.jixiFs4) jixiFs=JihuaParam.jixiFs4;
        double lixi=0;
        switch (jixiFs) {
            case JihuaParam.jixiFs1:
                if (qiciSegment!=JihuaParam.qiciLingQi){
                    lixi=fkje*(lilv/100)/30*days;
                }
                break;
            case JihuaParam.jixiFs2:
                lixiSum=fkje*lilv*qixian/100;//总利息
                daysSum=qixian*30;//总天数
                restDays=daysSum;//剩余天数
                canShu=(daysSum+1)*(daysSum/2);//等份参数
                lixiDengFen=lixiSum/canShu;//每份利息
                switch (qiciSegment) {
                    case JihuaParam.qiciShouQi:
                        RepaymentVariable.variable.get().firstDays=days;//记录第一期天数
//							lixi=lixiDengFen*(firstDays/2*((daysSum+(firstDays-1)*-1)+daysSum));
                        restDays=daysSum;
                        for (int i=0;i<RepaymentVariable.variable.get().firstDays;i++){
                            lixi=lixi+lixiDengFen*restDays;
                            restDays--;
                        }
                        break;
                    case JihuaParam.qiciMoQi:
//							lixi=lixiDengFen*((30-firstDays)/2*((30-firstDays)+1));
                        restDays=30-RepaymentVariable.variable.get().firstDays;

                        for (int i = 0; i <30-RepaymentVariable.variable.get().firstDays; i++) {
                            lixi=lixi+lixiDengFen*restDays;
                            restDays--;
                        }
                        break;
                    case JihuaParam.qiciZhongQi:
//							long firstTerm=firstDays+1+((qici-1)*30);//每期的首项
//							lixi=lixiDengFen*(30/2*((daysSum+(firstTerm-1)*-1)+(daysSum+(firstTerm+30-1)*-1)));
                        long firstTerm=RepaymentVariable.variable.get().firstDays+((qiciNext)*30);
                        restDays=daysSum- firstTerm;//每期开始剩余天数
                        for (int i = 0; i < 30; i++) {
                            lixi=lixi+lixiDengFen*restDays;
                            restDays--;
                        }
                        break;
                    default:
                        break;
                }

                break;
            case JihuaParam.jixiFs3:
                if(kouxiFs==JihuaParam.kouxiFs1){
                    if (qiciSegment==JihuaParam.qiciLingQi)
                        lixi = fkje * lilv * (double)qixian / 100.0D;
                }else if (kouxiFs==JihuaParam.kouxiFs2) {
                    if (qiciSegment==JihuaParam.qiciShouQi)
                        lixi = fkje * lilv * (double)qixian / 100.0D;
                }
                break;
            case JihuaParam.jixiFs4:
                if (qiciSegment!=JihuaParam.qiciLingQi){
                    lixi=fkje*(lilv/100)/30*days;
                }
                break;
            case JihuaParam.jixiFs21:
                lixiSum=fkje*lilv*qixian/100;//总利息
                daysSum=qixian*30;//总天数
                restDays=daysSum;//剩余天数
                canShu=(daysSum+1)*(daysSum/2);//等份参数
                lixiDengFen=lixiSum/canShu;//每份利息
                if(qiciSegment==JihuaParam.qiciShouQi){
                    RepaymentVariable.variable.get().firstDays=30;//每期30天计息
                    restDays=daysSum;
                    for (int i=0;i<RepaymentVariable.variable.get().firstDays;i++){
                        lixi=lixi+lixiDengFen*restDays;
                        restDays--;
                    }
                }else{
                    long firstTerm=RepaymentVariable.variable.get().firstDays+((qiciNext)*30);
                    restDays=daysSum- firstTerm;//每期开始剩余天数
                    for (int i = 0; i < 30; i++) {
                        lixi=lixi+lixiDengFen*restDays;
                        restDays--;
                    }
                }

                break;
            default:
                break;
        }

        //早偿，提前还款处理
        switch (lixiEntity.getSpecialPro()) {
            case JihuaParam.specialPro1:
                lixiSum=fkje*lilv/100*qixian;
                double lixiCharged=0;
                for (YizhiHkjihuaObj hkjh : RepaymentVariable.variable.get().lstHkjh) {
                    lixiCharged+=Double.parseDouble(hkjh.getYinghklx());
                }
                lixi=lixiSum-lixiCharged;
                break;
            case JihuaParam.specialPro2://早偿违约金目前按溢缴款处理
                if(kouxiFs==JihuaParam.kouxiFs2){//早偿利息已上扣
                    if(qiciSegment==JihuaParam.qiciMoQi)
                        lixi=0;
                }else if (kouxiFs==JihuaParam.kouxiFs1) {
                    if(jixiFs==JihuaParam.jixiFs2){
                        daysSum=qixian*30;//总天数
                        lixiSum=fkje*lilv*qixian/100;//总利息
                        canShu=(daysSum+1)*(daysSum/2);//等份参数
                        lixiDengFen=lixiSum/canShu;//每份利息
                        lixi=0;
                        long firstTerm=RepaymentVariable.variable.get().firstDays+((qiciNext)*30);
                        restDays=daysSum- firstTerm;//每期开始剩余天数
                        for (int i = 0; i < days; i++) {
                            lixi=lixi+lixiDengFen*restDays;
                            restDays--;
                        }
                    }
                }


                break;
            default:
                break;
        }
        lixi=new BigDecimal(lixi).setScale(2,RoundingMode.HALF_UP).doubleValue();
        return lixi;
    }

    public static double calFee(double fkje, double feeLv, long days, int feeFs, boolean koufBz) {

        double fee = 0.0D;
        switch(feeFs) {
            case JihuaParam.feeFs1:
                if(koufBz)
                    fee = fkje * feeLv / 100.0D;

                break;
            case JihuaParam.feeFs2:
                if(koufBz)
                    fee = fkje * (feeLv / 100.0D) / 30.0D * (double)days;
                break;

            default:
                break;
        }


        return fee;
    }



    public static double calBenjin(BenjinEntity benjinEntity, DateParam riqiParam) {
        double benjin=0;
        int jixiFs=benjinEntity.getJixiFs();
        int qixian=benjinEntity.getQixian();
        double fkje=benjinEntity.getFkje();
        double lilv=benjinEntity.getLilv();
        long days=benjinEntity.getDays();
        double lixi=benjinEntity.getLixi();
        boolean isLast=benjinEntity.isLast;

        String fkrq=riqiParam.getFkrq();
        String jsrq=riqiParam.jsrq;
        String ksrq=riqiParam.ksrq;
        String yhrq=riqiParam.yhrq;
        int jiesFs=riqiParam.getJiesFs();

        String year1=theYearToPay(fkrq, 12, jiesFs);
        String year2=theYearToPay(fkrq, 24, jiesFs);
        double lixiSum;//总利息
        double benLixi;//本金+总利息

        //早偿提前还款处理
        switch (benjinEntity.getSpecialPro()) {
            case JihuaParam.specialPro1:case JihuaParam.specialPro2:
                double benjCharged=0;
                for (YizhiHkjihuaObj hkjh : RepaymentVariable.variable.get().lstHkjh) {
                    benjCharged+=Double.parseDouble(hkjh.getYinghkbj());
                }
                return fkje-benjCharged;

            default:
                break;
        }

        //正常本金计算
        switch (jixiFs) {
            case JihuaParam.jixiFs1:case JihuaParam.jixiFs3:
                if(isLast) benjin=fkje;
                break;
            case JihuaParam.jixiFs2:
                lixiSum=fkje*lilv*qixian/100;//总利息
                benLixi=fkje+lixiSum;//本金+总利息
                int daysSum=qixian*30;//总天数
                double dengE=benLixi/daysSum;//每天等额
                benjin=days*dengE-lixi;
                break;
            case JihuaParam.jixiFs21:
                lixiSum=fkje*lilv*qixian/100;//总利息
                benLixi=fkje+lixiSum;//本金+总利息
                double dengEBenx=benLixi/qixian;
                dengEBenx=new BigDecimal(dengEBenx).setScale(2,RoundingMode.HALF_UP).doubleValue();
                benjin=dengEBenx-lixi;
                break;
            case JihuaParam.jixiFs41:

                if(jsrq.equals(year1)&&yhrq.equals(jsrq))
                    return  fkje * 0.15D;
                if(jsrq.equals(year2)&&yhrq.equals(jsrq))
                    return  fkje * 0.1D;
                if(isLast)
                    return  fkje * 0.75D;

                break;
            case JihuaParam.jixiFs42:

                if(jsrq.equals(year1)&&yhrq.equals(jsrq))
                    return  fkje * 0.1D;
                if(jsrq.equals(year2)&&yhrq.equals(jsrq))
                    return fkje * 0.1D;
                if(isLast)
                    return fkje * 0.8D;
                break;
            default:
                break;
        }

        return benjin;
    }



    public static int  jixiShouqiDays(DateParam riqiParam) {

        int kouxiFs=riqiParam.getKouxiFs();
        int jixiFs=riqiParam.getJixiFs();
        String ksri=riqiParam.getFkrq().substring(6,8);
        String hkri=riqiParam.getHkri();

        int jixiDays=0;
        switch (kouxiFs) {
            case JihuaParam.kouxiFs1:
                if (ksri.compareTo(hkri) == 0) {
                    jixiDays = 30;
                } else if (ksri.compareTo(hkri) < 0) {
                    jixiDays = Integer.parseInt(hkri)-Integer.parseInt(ksri);
                } else {
                    jixiDays = 30-Integer.parseInt(ksri)+Integer.parseInt(hkri);
                }
                break;
            case JihuaParam.kouxiFs2:
                if (ksri.compareTo(hkri) == 0) {
                    jixiDays = 30;
                } else if (ksri.compareTo("15") < 0) {
                    jixiDays = Integer.parseInt(hkri)-Integer.parseInt(ksri);
                } else {
                    jixiDays = 30-Integer.parseInt(ksri)+Integer.parseInt(hkri);
                }
                break;
            default:
                break;
        }

        //其他变动
        if(jixiFs==JihuaParam.jixiFs21){//等额还款固定30天区间规则
            jixiDays=30;
        }

        return jixiDays;
    }





    public static List<YizhiHkjihuaObj> genQici(List<YizhiHkjihuaObj> lstHkjh, DateParam riqiParam, BudgetEntity budgetEntity, String orderNo, int qici){
        YizhiHkjihuaObj hkjihua=new YizhiHkjihuaObj();

        hkjihua.setOrderno(orderNo);
        hkjihua.setQici(String.valueOf(qici));
        hkjihua.setKsriqi(riqiParam.ksrq);
        hkjihua.setJsriqi(riqiParam.jsrq);
        hkjihua.setYhkriqi(riqiParam.yhrq);
        hkjihua.setYinghkbj(new BigDecimal(budgetEntity.getBenj()).setScale(2, RoundingMode.HALF_UP).toString());
        hkjihua.setYinghklx(new BigDecimal(budgetEntity.getLixi()).setScale(2, RoundingMode.HALF_UP).toString());
        hkjihua.setYhfee(new BigDecimal(budgetEntity.getFee()).setScale(2, RoundingMode.HALF_UP).toString());
        hkjihua.setYhfwfee((new BigDecimal(budgetEntity.getFwfee())).setScale(2, RoundingMode.HALF_UP).toString());
        hkjihua.setYhqdffee((new BigDecimal(budgetEntity.getQdffee())).setScale(2, RoundingMode.HALF_UP).toString());

        lstHkjh.add(hkjihua);
        return lstHkjh;
    }

    private void genQici(DateParam riqiParam,BudgetEntity budgetEntity,String orderNo,int qici){
        YizhiHkjihuaObj hkjihua=new YizhiHkjihuaObj();
        hkjihua.setOrderno(orderNo);
        hkjihua.setQici(String.valueOf(qici));
        hkjihua.setKsriqi(riqiParam.ksrq);
        hkjihua.setJsriqi(riqiParam.jsrq);
        hkjihua.setYhkriqi(riqiParam.yhrq);
        hkjihua.setYinghkbj(new BigDecimal(budgetEntity.getBenj()).setScale(2, RoundingMode.HALF_UP).toString());
        hkjihua.setYinghklx(new BigDecimal(budgetEntity.getLixi()).setScale(2, RoundingMode.HALF_UP).toString());
        hkjihua.setYhfee(new BigDecimal(budgetEntity.getFee()).setScale(2, RoundingMode.HALF_UP).toString());
        hkjihua.setYhfwfee((new BigDecimal(budgetEntity.getFwfee())).setScale(2, RoundingMode.HALF_UP).toString());
        hkjihua.setYhqdffee((new BigDecimal(budgetEntity.getQdffee())).setScale(2, RoundingMode.HALF_UP).toString());

        RepaymentVariable.variable.get().lstHkjh.add(hkjihua);
    }
    private String getLastRiqi(DateParam riqiParam){
        String jsrq="";
        int jiesFs=riqiParam.getJiesFs();
        String fkrq=riqiParam.getFkrq();
        int qixian=riqiParam.getQixian();
        String hkri=riqiParam.getHkri();
        if(jiesFs==JihuaParam.jiesFs1){
            jsrq=DateUtil.getNextMonth(fkrq, qixian).substring(0,6)+hkri;
        }else if (jiesFs==JihuaParam.jiesFs2) {
            jsrq=DateUtil.getNextMonth(fkrq, qixian);
        }else if (jiesFs==JihuaParam.jiesFs3) {
            jsrq=DateUtil.getNextMonth(fkrq, qixian);
            jsrq=getNextDate(jsrq, -1);
        }
        return jsrq;
    }



    public static BigDecimal calWyjin(PayParam payParam, String hkr, String yhr,int qici,HolidayDao holidayDao) {
        double fkje=payParam.getFkje();
        int jixiFs=payParam.getJixiFs();
        int kouxiFs=payParam.getKouxiFs();

        long days = DateUtil.getDaysDiff(yhr, hkr);//超期天数
        BigDecimal bigZero = BigDecimal.ZERO;
        BigDecimal bigWyj = bigZero;
        if (qici == 0) {
            if (kouxiFs != JihuaParam.kouxiFs2 && jixiFs != JihuaParam.jixiFs3)//上扣息且一次收息需要违约金
                return bigZero;//零期为一次性收费，不计违约金
        }

        if (days <= 0)
            return bigZero;
        //节假日罚息业务
        YizhiHolidayObj holiday = holidayDao.sel_holiday(yhr);
        if (CommUtil.isNotNull(holiday)) {//应还日期不在节假日，照常收费
            int loop = 0;
            while (true) {
                ++loop;//天数累计
                String nextDate = getNextDate(yhr, loop);//获取下一天日期
                YizhiHolidayObj holiday1 = holidayDao.sel_holiday(nextDate);
                if (CommUtil.isNull(holiday1)) {//判断是否节假日,不是节假日还款照常收费
                    if (hkr.equals(nextDate)) return bigZero;
                    break;
                } else {
                    if (loop >= days) {//在节假日内还款，不收罚金
                        return bigZero;
                    }
                }
            }

        }

            /*
             * 若确实产生违约金则记录上次违约金日期
             */

            if (hkr.compareTo(RepaymentVariable.variable.get().payedWyjRiqi) > 0) {
                if (yhr.compareTo(RepaymentVariable.variable.get().payedWyjRiqi) < 0)
                    days = DateUtil.calDateDiff( RepaymentVariable.variable.get().payedWyjRiqi,hkr);

                bigWyj = BigDecimal.valueOf(fkje * 0.001 * days).setScale(2, RoundingMode.HALF_UP);
                RepaymentVariable.variable.get().payedWyjRiqi = hkr;
            } else {
                return bigZero;
            }
            return bigWyj;
        }

        public static double[] moPingYjk(BigDecimal yh, BigDecimal shih, double surplus){
            double cha=yh.subtract(shih).doubleValue();
            double[] surp=new double[2];
            if(surplus-cha>=0){
                surp[0]=0;
                surp[1]=surplus-=cha;
            }else{
                surp[0]=1;
                surp[1]=surplus;
            }
            return surp;
        }
    }
