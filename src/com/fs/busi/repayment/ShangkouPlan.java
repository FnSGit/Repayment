package com.fs.busi.repayment;

import com.fs.constants.repayment.FeeEnum;
import com.fs.constants.repayment.JihuaParam;
import com.fs.entity.repayment.entity.BenjinEntity;
import com.fs.entity.repayment.entity.BudgetEntity;
import com.fs.entity.repayment.entity.FeeEntity;
import com.fs.entity.repayment.entity.LixiEntity;
import com.fs.entity.repayment.param.DateParam;
import com.fs.entity.repayment.param.PayParam;
import com.fs.generate.target.entity.YizhiFkxxObj;
import com.fs.generate.target.entity.YizhiHkjihuaObj;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShangkouPlan extends PlanFactory {

    public ShangkouPlan(PayParam payParam) {
        super(payParam);
    }

    @Override
    public List<YizhiHkjihuaObj> getPlan() {
        List<YizhiHkjihuaObj> hkjihuaObjList = new ArrayList<>();

        //取得参数信息
        int kouxiFs=payParam.getKouxiFs();
        int jixiFs=payParam.getJixiFs();
        int jiesFs=payParam.getJiesFs();
        Map<FeeEnum, Integer> feeFs = payParam.getFeeFsMap();
        Map<FeeEnum, Double> feeLvMap =payParam.getFeeLvMap();
        int special=payParam.getSpecialPro();
        //取得还款信息
        double fkje=payParam.getFkje();//放款金额
        String fkrq=payParam.getFkrq();//放款日期
        String orderNo=payParam.getOrderno();

        double lilv=payParam.getLilv();//利率
//        double fwflv =payParam.getFeeLvMap().get(FeeEnum.fwfFee);
//        feeLvMap.put(FeeEnum.fwfFee, fwflv);
//        double qudfflv = fkxx.getQudfflv().doubleValue();
//        feeLvMap.put(FeeEnum.qdfFee, qudfflv);
        int qixian=payParam.getQixian();//还款总期限（单位：月）

        String ksrq="";/*开始日期*/
        String jsrq = "";//结束日期
        String yhrq = "";//应还日期


        int lixiMonth=30;//利息每月按30
        String hkri="20";//每期20号还款

        String ksri = fkrq.substring(6,8);//首期日

        int qiciSize=qixian-1;//从零期开始需减一
        //期数碰整
        if(!ksri.equals(hkri)&&jixiFs!=JihuaParam.jixiFs21){//首期不为还款日则需补期
            ++qiciSize;
        }
        //月息年本计息方式
        int yxnbJixi=0;
        if(jixiFs/10==JihuaParam.jixiFs4)
            yxnbJixi=JihuaParam.jixiFs4;
        //期数上扣息处理
        if (kouxiFs==JihuaParam.kouxiFs2&&jixiFs!=JihuaParam.jixiFs21){//上扣息末期收利息及本金有俩还款日，多一期，等额除外
            ++qiciSize;
            if(ksri.compareTo("15")>=0&&ksri.compareTo("20")<0)//上扣息15-19计息天数超过30，第一期看作两期，不用补期。
                --qiciSize;
            if(yxnbJixi==JihuaParam.jixiFs4)
                if(qixian>=12&&qixian<24){
                    qiciSize+=1;
                }else if (qixian>24&&qixian<=36) {
                    qiciSize+=2;
                }
        }

        //月息年本中间期还本日期处理
        String year1=RepayTool.theYearToPay(fkrq, 12, jiesFs);//第一年末
        String year2=RepayTool.theYearToPay(fkrq, 24, jiesFs);//第二年末

        DateParam dateParam = new DateParam(payParam);
        for (int i=0;i<=qiciSize;++i){
            double yhbj=0;//应还本金
            double lixi=0;//每期的利息
            double fee=0;
            double fwfFee = 0.0D;
            double qudfFee = 0.0D;
            LixiEntity lixiEntity=new LixiEntity(payParam);//默认计息天数30
            BenjinEntity benjinEntity=new BenjinEntity(payParam);//默认天数30
//            FeeEntity feeEntity=new FeeEntity(fkje, qixian, feeFs, feeLvMap);
            BudgetEntity budgetEntity=new BudgetEntity(payParam);
            int jixiDays=0;
            if (i == 0) {//第零期开始计息
                //生成日期
                dateParam=buildRiqi(dateParam, JihuaParam.qiciShouQi);
                ksrq = dateParam.ksrq;//开始日=放款日期
                jsrq=dateParam.jsrq;
                yhrq=dateParam.yhrq;
                //计息日期
                jixiDays=jixiShouqiDays(dateParam);

                //利息，本金，费用
                lixiEntity.days = (long)jixiDays;
                lixi = this.calLixi(lixiEntity,JihuaParam.qiciShouQi);
                benjinEntity.days = (long)jixiDays;
                benjinEntity.lixi = lixi;
                yhbj = this.calBenjin(benjinEntity,dateParam);
                //计算费用

                int fwfFs=(Integer)feeFs.get(FeeEnum.fwfFee);

                fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);

                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
                qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
                fee = fwfFee + qudfFee;

            }else if (i==qiciSize-1) {//倒数第二期，一般用于末期俩还款日拆分
                if (kouxiFs==JihuaParam.kouxiFs2&&jixiFs!=JihuaParam.jixiFs21) {
                    dateParam=buildRiqi(dateParam, JihuaParam.qiciMoQi);
                    ksrq=dateParam.ksrq;//开始日期等于上次结束日期
                    jsrq=dateParam.jsrq;
                    yhrq=ksrq;

                    if(ksri.equals(hkri)) {
                        jixiDays=30;
                    }else{
                        if (kouxiFs==JihuaParam.kouxiFs2&&(ksri.compareTo("15")>=0&&ksri.compareTo("19")<=0)) {
                            jixiDays=60-jixiShouqiDays(dateParam);//利息按首尾差处理,首期超30天
                        }else {
                            jixiDays=30-jixiShouqiDays(dateParam);//利息按首尾差处理
                        }

                    }

                    //上扣最后一期不计本金，计利息
                    lixiEntity.days=jixiDays;
                    lixi = calLixi(lixiEntity,JihuaParam.qiciMoQi);
                    if (special==JihuaParam.specialPro3) {
                        lixi=fkxx.getExtrmony().doubleValue();
                    }
                    //计算费用
                    int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
                    int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);

                    if(fwfFs==JihuaParam.feeFs2)
                        fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
                    if(qdfFs==JihuaParam.feeFs2)
                        qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
                    if (special==JihuaParam.specialPro5) {
                        fwfFee=fkxx.getExtrmony().doubleValue();
                    }
                    fee = fwfFee + qudfFee;
                }else{
                    dateParam=buildRiqi(dateParam, JihuaParam.qiciZhongQi);
                    ksrq=dateParam.ksrq;//开始日期等于上次结束日期
                    jsrq = dateParam.jsrq;//结束日期为下月当日
                    yhrq=dateParam.yhrq;

                    lixi = calLixi(lixiEntity,JihuaParam.qiciZhongQi);//利息每月按30天计算
                    benjinEntity.lixi=lixi;
                    yhbj=calBenjin(benjinEntity,dateParam);
                    //计算费用
                    int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
                    int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
                    if(fwfFs==JihuaParam.feeFs2)
                        fwfFee = this.calFee(fkje, fwflv, lixiMonth, fwfFs, true);
                    if(qdfFs==JihuaParam.feeFs2)
                        qudfFee = this.calFee(fkje, qudfflv, lixiMonth, qdfFs, true);
                    fee = fwfFee + qudfFee;
                }

            } else if (i==qiciSize){//最后一期

                if(kouxiFs==JihuaParam.kouxiFs2&&jixiFs!=JihuaParam.jixiFs21){
                    dateParam=buildRiqi(dateParam, JihuaParam.qicijiBen);

                    ksrq=dateParam.ksrq;
                    jsrq=dateParam.jsrq;
                    yhrq=dateParam.yhrq;;

                    benjinEntity.isLast=true;
                    yhbj=calBenjin(benjinEntity,dateParam);

                }else{
                    dateParam=buildRiqi(dateParam, JihuaParam.qiciMoQi);

                    ksrq=dateParam.ksrq;//开始日期等于上次结束日期
                    jsrq=dateParam.jsrq;
                    yhrq=dateParam.yhrq;;

                    if(ksri.equals(hkri)) {
                        jixiDays=30;
                    }else{
                        if(jixiFs==JihuaParam.jixiFs21){
                            jixiDays=30;
                        }else{
                            if (kouxiFs==JihuaParam.kouxiFs2&&(ksri.compareTo("15")>=0&&ksri.compareTo("19")<=0)) {
                                jixiDays=60-jixiShouqiDays(dateParam);//利息按首尾差处理,首期超30天
                            }else {
                                jixiDays=30-jixiShouqiDays(dateParam);//利息按首尾差处理
                            }

                        }
                    }
                    lixiEntity.days=jixiDays;
                    benjinEntity.days=jixiDays;
                    lixi = calLixi(lixiEntity,JihuaParam.qiciMoQi);
                    benjinEntity.lixi=lixi;
                    benjinEntity.isLast=true;
                    yhbj=calBenjin(benjinEntity,dateParam);
                    //计算费用
                    int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
                    int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
                    if(fwfFs==JihuaParam.feeFs2)
                        fwfFee = this.calFee(fkje, fwflv, jixiDays, fwfFs, true);
                    if(qdfFs==JihuaParam.feeFs2)
                        qudfFee = this.calFee(fkje, qudfflv, jixiDays, qdfFs, true);
                    fee = fwfFee + qudfFee;
                }
            }else {//中间期
                //月息年本中间期本金收取
                if(kouxiFs==JihuaParam.kouxiFs2&&yxnbJixi==JihuaParam.jixiFs4){
                    if(dateParam.jsrq.compareTo(year1)>=0&&dateParam.ksrq.compareTo(year1)<0){
                        String yearKsrq=dateParam.ksrq;
                        String yearJsrq=dateParam.jsrq;
                        dateParam.jsrq=year1;
                        dateParam=buildRiqi(dateParam, JihuaParam.qicijiBen);
                        budgetEntity=budget(budgetEntity,JihuaParam.qicijiBen, dateParam, lixiEntity, benjinEntity, feeEntity, 0);
                        hkjihuaObjList=genQici( hkjihuaObjList,dateParam, budgetEntity, orderNo, i);
                        ++i;

                        //中间期
                        dateParam.ksrq=yearKsrq;
                        dateParam.jsrq=yearJsrq;
                        dateParam=buildRiqi(dateParam, JihuaParam.qiciZhongQi);
                        budgetEntity=budget(budgetEntity,JihuaParam.qiciZhongQi, dateParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
                        hkjihuaObjList=genQici( hkjihuaObjList,dateParam, budgetEntity, orderNo, i);
                        continue;
                    }else if (dateParam.jsrq.compareTo(year2)>=0&&dateParam.ksrq.compareTo(year2)<0) {
                        String yearKsrq=dateParam.ksrq;
                        String yearJsrq=dateParam.jsrq;
                        dateParam.jsrq=year2;
                        dateParam=buildRiqi(dateParam, JihuaParam.qicijiBen);
                        budgetEntity=budget(budgetEntity,JihuaParam.qicijiBen, dateParam, lixiEntity, benjinEntity, feeEntity, 0);
                        hkjihuaObjList=genQici( hkjihuaObjList,dateParam, budgetEntity, orderNo, i);
                        ++i;

                        //中间期
                        dateParam.ksrq=yearKsrq;
                        dateParam.jsrq=yearJsrq;
                        dateParam=buildRiqi(dateParam, JihuaParam.qiciZhongQi);
                        budgetEntity=budget(budgetEntity,JihuaParam.qiciZhongQi, dateParam, lixiEntity, benjinEntity, feeEntity, lixiMonth);
                        hkjihuaObjList=genQici( hkjihuaObjList,dateParam, budgetEntity, orderNo, i);
                        continue;
                    }
                }

                dateParam=buildRiqi(dateParam, JihuaParam.qiciZhongQi);
                ksrq=dateParam.ksrq;//开始日期等于上次结束日期
                jsrq = dateParam.jsrq;//结束日期为下月当日
                yhrq=dateParam.yhrq;

                lixi = calLixi(lixiEntity,JihuaParam.qiciZhongQi);//利息每月按30天计算
                benjinEntity.lixi=lixi;
                yhbj=calBenjin(benjinEntity,dateParam);
                //计算费用
                int fwfFs= (Integer)feeFs.get(FeeEnum.fwfFee);
                int qdfFs= (Integer)feeFs.get(FeeEnum.qdfFee);
                if(fwfFs==JihuaParam.feeFs2)
                    fwfFee = this.calFee(fkje, fwflv, lixiMonth, fwfFs, true);
                if(qdfFs==JihuaParam.feeFs2)
                    qudfFee = this.calFee(fkje, qudfflv, lixiMonth, qdfFs, true);
                fee = fwfFee + qudfFee;
            }
//	            System.out.println("期次："+i+",开始日期："+ksrq+",结束日期："+jsrq+",应还款日期："+yhrq+",应还本金："+yhbj+",应还利息："+lixi+",应还费用："+fee);
            yizhi_hkjihua hkjihua=SysUtil.getInstance(yizhi_hkjihua.class);

            hkjihua.setOrderno(fkxx.getOrderno());
            hkjihua.setQici(String.valueOf(i));
            hkjihua.setKsriqi(ksrq);
            hkjihua.setJsriqi(jsrq);
            hkjihua.setYhkriqi(yhrq);
            hkjihua.setYinghkbj(new BigDecimal(yhbj).setScale(2, RoundingMode.HALF_UP));
            hkjihua.setYinghklx(new BigDecimal(lixi).setScale(2, RoundingMode.HALF_UP));
            hkjihua.setYhfee(new BigDecimal(fee).setScale(2, RoundingMode.HALF_UP));
            hkjihua.setYhfwfee((new BigDecimal(fwfFee)).setScale(2, RoundingMode.HALF_UP));
            hkjihua.setYhqdffee((new BigDecimal(qudfFee)).setScale(2, RoundingMode.HALF_UP));

            /*
             * 还款日期相同合并
             */
//	           yizhi_hkjihua hkjihuaLast=hkjihuaObjList.get(i);
            yizhi_hkjihua hkjihuaLastSecond=null;
            if (i>0) {
                hkjihuaLastSecond=hkjihuaObjList.get(i-1);
                if(hkjihua.getYhkriqi().equals(hkjihuaLastSecond.getYhkriqi())){
                    hkjihuaLastSecond.setYinghkbj(hkjihua.getYinghkbj().add(hkjihuaLastSecond.getYinghkbj()));
                    hkjihuaLastSecond.setYinghklx(hkjihua.getYinghklx().add(hkjihuaLastSecond.getYinghklx()));
                    hkjihuaLastSecond.setYhfee(hkjihua.getYhfee().add(hkjihuaLastSecond.getYhfee()));
                    hkjihuaLastSecond.setYhfwfee(hkjihua.getYhfwfee().add(hkjihuaLastSecond.getYhfwfee()));
                    hkjihuaLastSecond.setYhqdffee(hkjihua.getYhqdffee().add(hkjihuaLastSecond.getYhqdffee()));

                    hkjihuaObjList.remove(i-1);
                    hkjihuaObjList.add(hkjihuaLastSecond);
                }else {
                    hkjihuaObjList.add(hkjihua);
                }
            }else {
                hkjihuaObjList.add(hkjihua);
            }

        }
	     /*   yizhi_hkjihua hkjihuaLast=hkjihuaObjList.get(qiciSize);
	        yizhi_hkjihua hkjihuaLastSecond=hkjihuaObjList.get(qiciSize-1);
	        if(hkjihuaLast.getYhkriqi().equals(hkjihuaLastSecond.getYhkriqi())){
	        	hkjihuaLastSecond.setYinghkbj(hkjihuaLast.getYinghkbj().add(hkjihuaLastSecond.getYinghkbj()));
	        	hkjihuaLastSecond.setYinghklx(hkjihuaLast.getYinghklx().add(hkjihuaLastSecond.getYinghklx()));
	        	hkjihuaLastSecond.setYhfee(hkjihuaLast.getYhfee().add(hkjihuaLastSecond.getYhfee()));
	        	hkjihuaLastSecond.setYhfwfee(hkjihuaLast.getYhfwfee().add(hkjihuaLastSecond.getYhfwfee()));
	        	hkjihuaLastSecond.setYhqdffee(hkjihuaLast.getYhqdffee().add(hkjihuaLastSecond.getYhqdffee()));

	        	hkjihuaObjList.remove(qiciSize);
	        }*/

        return hkjihuaObjList;
    }
}
